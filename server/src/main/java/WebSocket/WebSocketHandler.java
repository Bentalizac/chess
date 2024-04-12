package WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorNotification;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;


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
            case MAKE_MOVE ->  makeMove(new MakeMoveCommand( action.getAuthString(), action.getMove(), action.getGameID()), session);
            case LEAVE -> leave(new LeaveCommand(action.getAuthString(), action.getGameID()), session);
            case RESIGN -> resign(new ResignCommand(action.getAuthString(), action.getGameID()), session);
        }
    }

    private void spectate(SpectateCommand request, Session session) throws IOException {

        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;
        connections.add(request.getAuthString(), session, request.getGameID());

        if (auth == null) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
            System.out.print("Boo");
        }

        var message = username + " has started stalking this game\n";
        var notification = new Notification(message);
        connections.broadcast(auth.authToken(), notification, request.getGameID());

        var game = dataAccess.getGame(request.getGameID());
        if (game == null) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Game not found, check your game ID again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, request.getPlayerColor());
        connections.sendMessage(auth.authToken(), response);
    }

    private void leave(LeaveCommand request, Session session) throws IOException {

        connections.add(request.getAuthString(), session, request.getGameID());
        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;

        if(auth == null){
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        var message = username + " Has left the game. What a loser.\n";
        System.out.print(message);
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification, request.getGameID());
        connections.remove(request.getAuthString());
        connections.remove(username);
    }

    public void resign(ResignCommand request, Session session) throws IOException{
        connections.add(request.getAuthString(), session, request.getGameID());
        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;

        if(auth == null){
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        GameData data = dataAccess.getGame(request.getGameID());
        String victor;


        if(!Objects.equals(username, data.whiteUsername()) && !Objects.equals(username, data.blackUsername())) {
            connections.sendMessage(request.getAuthString(),  new ErrorNotification("ERROR: You can't surrender on behalf of others.\n"));
            return;
        }

        if (data.victor() != null) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: This game is already over.\n"));
            return;
        }

        if(Objects.equals(username, data.whiteUsername())) {
            victor = data.blackUsername();
        }
        else{
            victor = data.whiteUsername();
        }

        try {
            dataAccess.updateGame(new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), data.game(), victor));
        }
        catch (ResponseException ex) {

            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: The system didn't accept your cowardice. No retreat. No surrender.\n"));
            return;
        }
        var message = username + " Has resigned. May dishonor be upon them, upon their family, and upon their cattle.\n";
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification, request.getGameID());
        connections.sendMessage(auth.authToken(), notification);




    }

    private void join(JoinCommand request,Session session) throws IOException {
        connections.add(request.getAuthString(), session, request.gameID());
        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;

        if(auth == null){
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        var message = username + " Has joined the game playing " + request.getColor() + "\n";
        System.out.print(message);
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification, request.gameID());

        var game = dataAccess.getGame(request.getGameID());
        if (game == null) { // gameID not found
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Game not found, check your game ID again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        // color already taken by someone else
        if ((game.whiteUsername()!=null && !game.whiteUsername().equals(username) && request.getColor() == ChessGame.TeamColor.WHITE) || (game.blackUsername() != null && !game.blackUsername().equals(username) && request.getColor() == ChessGame.TeamColor.BLACK) ){

            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: That seat is already taken, find another.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        // game not initialized TODO ask TAs about this one.
        if (Objects.equals(game.whiteUsername(), null) && Objects.equals(game.blackUsername(),null)) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: That game hasn't started yet. Wait, how'd you even get in here?.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, request.getColor());
        connections.sendMessage(auth.authToken(), response);
    }

    private void makeMove(MakeMoveCommand request, Session session) throws IOException {
        connections.add(request.getAuthString(), session, request.getGameID());
        AuthData auth = dataAccess.getUserByAuth(request.getAuthString());
        String username;
        if(auth == null){
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: Action unauthorized. Please try logging out and back in again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        else{
            username = auth.username();
        }

        ChessMove move = request.getMove();
        GameData data = dataAccess.getGame(request.getGameID());

        ChessGame game = data.game();
        ChessGame.TeamColor sendingColor;
        String activePlayer;

        if(game.getTeamTurn() == ChessGame.TeamColor.WHITE) {
            activePlayer = data.whiteUsername();
            sendingColor = ChessGame.TeamColor.WHITE;
        }
        else{
            activePlayer = data.blackUsername();
            sendingColor = ChessGame.TeamColor.BLACK;
        }

        if(data.victor() != null) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: " + data.victor() + "already won this game.\n"));
            return;
        }

        if (!Objects.equals(username, activePlayer)) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: You can't move someone else's piece.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }

        try {
            game.makeMove(move);
        }
        catch(InvalidMoveException ex) {
            connections.sendMessage(request.getAuthString(), new ErrorNotification("ERROR: That move is invalid, please try again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        try {
            dataAccess.updateGame(new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), game, null));
        }
        catch(ResponseException ex){
            connections.sendMessage(request.getAuthString(),  new ErrorNotification("ERROR: That move didn't go through, try again.\n"));
            connections.remove(request.getAuthString()); // Disconnect erroneous connection
            return;
        }
        var message = username + "moves " + move.getStartPosition() + " to " + move.getEndPosition() + "\n";
        System.out.print(message);
        var notification = new webSocketMessages.serverMessages.Notification(message);
        connections.broadcast(auth.authToken(), notification, request.getGameID());

        var response = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, data, sendingColor);
        connections.sendMessage(auth.authToken(), response);
        connections.broadcast(auth.authToken(), response, request.getGameID());
    }





}