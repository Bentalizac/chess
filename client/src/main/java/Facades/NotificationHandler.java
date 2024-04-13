package Facades;


import chess.BoardPainter;
import chess.ChessGame;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {
    public default void notify(String message) {};

}
