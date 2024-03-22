package UserInterface;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.EscapeSequences.*;

public class boardPainter {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";

    private ChessBoard board;
    public boardPainter(ChessBoard inBoard) {
        board = inBoard;
    }

    private String getPieceAtCoords(int x, int y){
        ChessPiece piece  =  board.getPiece(new ChessPosition(y, x));
        if (piece == null) {
            return " ";
        }
        return piece.toString();
    }

    private String buildBlackSquare(int x, int y){
        String output = SET_BG_COLOR_BLACK + " ";
        output += getPieceAtCoords(x, y) + " ";
        return output+ SET_BG_COLOR_DARK_GREY;
    }

    private String buildWhiteSquare(int x, int y) {
        String output = SET_BG_COLOR_WHITE + " ";
        output += getPieceAtCoords(x, y) + " ";
        return output + SET_BG_COLOR_DARK_GREY;
    }


    public String drawBlackDown() {
        String output = "\n";
        for (int y = 1; y <9; y++){
            output += " " + y + " ";
            for (int x = 8; x >= 1; x--){
                if(y%2 == 0) { // Even rows
                    if(x%2 == 0){
                        output += buildBlackSquare(x, y);
                    }
                    else{
                        output += buildWhiteSquare(x, y);
                    }
                }
                else{
                    if(x%2 == 1){
                        output += buildBlackSquare(x, y);
                    }
                    else{
                        output += buildWhiteSquare(x, y);
                    }
                }
            }
            output += "\n" + SET_BG_COLOR_DARK_GREY;
        }
        output += "    h  g  f  e  d  c  b  a \n";
        System.out.print(output);
        return output;
    }

    public String drawWhiteDown() { // Returns the drawn and colored output for the white on bottom alignment
        String output = "\n";
        for (int y = 8; y >= 1; y--){
            output += " " + (y) + " ";
            for (int x = 1; x < 9; x++){
                if(y%2 == 0) { // Even rows
                    if(x%2 == 0){
                        output += buildBlackSquare(x, y);
                    }
                    else{
                        output += buildWhiteSquare(x, y);
                    }
                }
                else{
                    if(x%2 == 1){
                        output += buildBlackSquare(x, y);
                    }
                    else{
                        output += buildWhiteSquare(x, y);
                    }
                }
            }
            output += "\n" + SET_BG_COLOR_DARK_GREY;
        }
        output += "    a  b  c  d  e  f  g  h \n";
        System.out.print(output);
        return output;
    }

}
