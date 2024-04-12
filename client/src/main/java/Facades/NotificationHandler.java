package Facades;


import chess.BoardPainter;
import chess.ChessGame;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public class NotificationHandler {
    public void notify(Notification notification) {
        System.out.println(notification.getMessage());
        printPrompt();
    }

    public void loadGame(LoadGameMessage input) {
        System.out.println(input.getMessage());
        drawBoard(input, input.getColor());

    }

    private void printPrompt() {
        System.out.print("\n"  + ">>> ");
    }

    private void drawBoard(Notification input, ChessGame.TeamColor color){
        ChessGame game = new Gson().fromJson(input.getGame(), ChessGame.class);
        BoardPainter painter = new BoardPainter(game.getBoard());
        if(color == ChessGame.TeamColor.BLACK){
            System.out.print(painter.drawBlackDown());
        }
        else{
            painter.drawWhiteDown();
        }
    }
}
