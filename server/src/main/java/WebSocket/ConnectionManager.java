package WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer>  gameConnections = new ConcurrentHashMap<>();

    public void add(String authToken, Session session, int gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameConnections.put(authToken, gameID);
    }
    public void remove(String authToken) {
        connections.remove(authToken);
        gameConnections.remove(authToken);
    }
    public void sendMessage(String username, ServerMessage message) throws IOException {
        var c = connections.get(username);
        if (c.session.isOpen()) {
            if (c.visitorName.equals(username)) {
                c.send(message.toString());
            }
        }
    }

    public void broadcast(String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName) && gameConnections.get(c.visitorName) == gameID) {
                    c.send(notification.toString());
                }
            }
        }
    }
}
