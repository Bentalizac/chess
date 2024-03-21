import chess.*;
import server.ServerFacade;
import ui.EscapeSequences;

public class Main {


    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverFacade = new ServerFacade(8080);



        while(true) {

        }


    }
}