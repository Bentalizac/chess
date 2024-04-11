package WebSocket;

import com.google.gson.Gson;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorNotification;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinCommand;
import webSocketMessages.userCommands.SpectateCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final MySQLDataAccess dataAccess;

    public WebSocketHandler() {
        this.dataAccess  = new MySQLDataAccess();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_OBSERVER -> spectate(new SpectateCommand(action.getAuthString(), action.getGameID()), session);
            case JOIN_PLAYER ->  join(new JoinCommand(action.getAuthString(), action.getGameID(), action.getPlayerColor()), session);
        }
    }

    private void spectate(SpectateCommand input, Session session) throws IOException {

        String username = dataAccess.getUserByAuth(input.getAuthString()).username();

        connections.add(username, session);
        var message = username + " has started stalking this game\n";
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(username, notification);

        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dataAccess.getGame(input.gameID()));
        connections.sendMessage(username, response);
    }

    private void join(JoinCommand join,Session session) throws IOException {
        connections.add(join.getAuthString(), session);
        AuthData auth = dataAccess.getUserByAuth(join.getAuthString());
        String username;

        if(auth == null){
            var error = new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n");
            System.out.print(error.getMessage());
            connections.sendMessage(join.getAuthString(), error);
            connections.remove(join.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        var message = username + " Has joined the game playing " + join.getColor() + "\n";
        System.out.print(message);
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(username, notification);

        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dataAccess.getGame(join.gameID()));
        connections.sendMessage(username, response);

    }

}