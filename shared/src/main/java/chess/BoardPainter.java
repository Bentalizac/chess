package chess;

import java.util.ArrayList;

import static chess.EscapeSequences.*;

public class BoardPainter {

    private final ChessBoard board;
    public BoardPainter(ChessBoard inBoard) {
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
    private String buildHighlightSquare(int x, int y) {
        String output = SET_BG_COLOR_GREEN + " ";
        output += getPieceAtCoords(x, y) + " ";
        return output + SET_BG_COLOR_DARK_GREY;
    }

    private String buildSquare(int x, int y, boolean isHighLight){
        StringBuilder output = new StringBuilder();
        if (isHighLight){
            output.append(buildHighlightSquare(x, y));
            return output.toString();
        }
        if(y%2 == 0) { // Even rows
            if(x%2 == 0){
                output.append(buildBlackSquare(x, y));
            }
            else{
                output.append(buildWhiteSquare(x, y));
            }}

        else{
            if(x%2 == 1){
                output.append(buildBlackSquare(x, y));
            }
            else{
                output.append(buildWhiteSquare(x, y));
            }}
        return output.toString();
    }


    public String drawBlackDown() {
        StringBuilder output = new StringBuilder("\n");
        for (int y = 1; y < 9; y++) {
            output.append(" ").append(y).append(" ");
            for (int x = 8; x >= 1; x--) {
                output.append(buildSquare(x, y, false));
                output.append("\n" + SET_BG_COLOR_DARK_GREY);
            }
        }
        output.append("    h  g  f  e  d  c  b  a \n");
        System.out.print(output);
        return output.toString();
    }



    public void drawWhiteDown() { // Returns the drawn and colored output for the white on bottom alignment
        StringBuilder output = new StringBuilder("\n");
        for (int y = 8; y >= 1; y--){
            output.append(" ").append(y).append(" ");

            for (int x = 1; x < 9; x++){
                output.append(buildSquare(x, y, false) );
            }

            output.append("\n" + SET_BG_COLOR_DARK_GREY);
        }
        output.append("    a  b  c  d  e  f  g  h \n");
        System.out.print(output);
    }

    public void highlight(ChessPosition position, ChessGame game) {
        var validMoves = board.getPieceMoves(position);
        var endpositions = new ArrayList<ChessPosition>();

        var color = game.getTeamTurn();
        for (ChessMove move : validMoves) {
            endpositions.add(move.getEndPosition());
        }
        StringBuilder output = new StringBuilder("\n");
        for (int y = 8; y >= 1; y--) {
            output.append(" ").append(y).append(" ");
            for (int x = (color == ChessGame.TeamColor.WHITE) ? 1 : 8;
                 (color == ChessGame.TeamColor.WHITE) ? x < 9 : x >= 1;
                 x += (color == ChessGame.TeamColor.WHITE) ? 1 : -1) {
                ChessPosition currentPos = new ChessPosition(x, y);
                output.append(buildSquare(x, y, endpositions.contains(currentPos)));
            }
            output.append("\n" + SET_BG_COLOR_DARK_GREY);
        }
        output.append("    ");
        output.append((color == ChessGame.TeamColor.WHITE) ? "a  b  c  d  e  f  g  h \n" : "h  g  f  e  d  c  b  a \n");
        System.out.print(output);
    }


}
