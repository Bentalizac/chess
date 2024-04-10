package WebSocket;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.notifications.Action;
import webSocketMessages.notifications.Notification;
import webSocketMessages.userCommands.SpectateCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        switch (action.type()) {
            case JOIN_OBSERVER -> spectate( new SpectateCommand(action.auth()), session);
        }
    }

    private void spectate(SpectateCommand input, Session session) throws IOException {
        connections.add(input.getUsername(), session);
        var message = input.getUsername() + " has started stalking this game\n";
        var notification = new Notification(Notification.Type.JOIN_OBSERVER, message);
        connections.broadcast(input.getUsername(), notification);
    }


}