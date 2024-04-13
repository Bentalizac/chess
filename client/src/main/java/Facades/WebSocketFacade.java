package Facades;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    Session session;
    NotificationHandler notificationHandler;
    public ChessGame game;
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
                    notificationHandler.notify(message);
                }
            });
        }
        catch(DeploymentException | IOException | URISyntaxException ex) {
                throw new ResponseException(500, ex.getMessage());
        }

    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void spectate(AuthData data, int gameID) throws ResponseException {
        try {
            onOpen(session, null);
            var action = new SpectateCommand(data.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void join(AuthData data, ChessGame.TeamColor color, int gameID) throws ResponseException {
        try {
            var action = new JoinCommand(data.authToken(), gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void move(AuthData data, ChessMove move, int gameID) throws ResponseException {
        try {
            var action = new MakeMoveCommand(data.authToken(), move, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(AuthData data, int gameID) throws ResponseException {
        try {
            var action = new LeaveCommand(data.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}
