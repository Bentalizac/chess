package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import webSocketMessages.notifications.Action;
import webSocketMessages.notifications.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    Session session;
    NotificationHandler notificationHandler;
    public WebSocketFacade(int port, NotificationHandler nh) throws ResponseException {
        try {
            String url = "ws://localhost:" + port;
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = nh;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        }
        catch(DeploymentException | IOException | URISyntaxException ex) {
                ex.printStackTrace();
                throw new ResponseException(500, ex.getMessage());
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void spectate(AuthData data) throws ResponseException {
        try {
            var action = new Action(Action.Type.JOIN_OBSERVER, data);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}
