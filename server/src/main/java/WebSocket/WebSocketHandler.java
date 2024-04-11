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

    private void spectate(SpectateCommand request, Session session) throws IOException {

        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;
        connections.add(request.getAuthString(), session);

        if (auth == null) {
            var error = new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n");
            connections.sendMessage(request.getAuthString(), error);
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        var message = username + " has started stalking this game\n";
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification);

        var game = dataAccess.getGame(request.gameID());
        if (game == null) {
            var error = new ErrorNotification("ERROR: Game not found, check your game ID again.\n");
            connections.sendMessage(request.getAuthString(), error);
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.sendMessage(auth.authToken(), response);
    }

    private void join(JoinCommand request,Session session) throws IOException {
        connections.add(request.getAuthString(), session);
        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;

        if(auth == null){
            var error = new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n");
            System.out.print(error.getMessage());
            connections.sendMessage(request.getAuthString(), error);
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        var message = username + " Has joined the game playing " + request.getColor() + "\n";
        System.out.print(message);
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification);

        var game = dataAccess.getGame(request.gameID());
        if (game == null) {
            var error = new ErrorNotification("ERROR: Game not found, check your game ID again.\n");
            connections.sendMessage(request.getAuthString(), error);
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        if ((game.whiteUsername()!=null && !game.whiteUsername().equals(username)) || (game.blackUsername() != null && !game.blackUsername().equals(username)) ){
            var error = new ErrorNotification("ERROR: That seat is already taken, find another.\n");
            connections.sendMessage(request.getAuthString(), error);
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.sendMessage(auth.authToken(), response);

    }

}