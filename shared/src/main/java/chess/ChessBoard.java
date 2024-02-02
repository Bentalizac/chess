package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] spaces;

    private ArrayList<ChessPosition> whitePieces = new ArrayList<>();
    private ArrayList<ChessPosition> blackPieces = new ArrayList<>();

    public ChessBoard() {

        this.spaces = new ChessPiece[9][9];
    }

    public ChessPiece[][] getSpaces() {
        return spaces;
    }

    public void setSpaces(ChessPiece[][] spaces) {
        this.spaces = spaces;
    }

    public ArrayList<ChessPosition> getWhitePieces() {
        return this.whitePieces;
    }


    public ArrayList<ChessPosition> getBlackPieces() {
        return this.blackPieces;
    }

    public void updateArrays(ChessMove move, ChessPiece piece){ //Updates the arrays storing the location of each team's pieces. Called from makeMove
        //list.set(list.indexOf(oldObject), newObject);
        switch(piece.getTeamColor()){ //list.set(list.indexOf(oldObject), newObject);
            case WHITE:
                this.whitePieces.remove(move.getStartPosition());
                this.whitePieces.add(move.getEndPosition());
                break;
            case BLACK:
                this.blackPieces.remove(move.getStartPosition());
                this.blackPieces.add(move.getEndPosition());
                break;
        }
    }
    public void capturePiece(ChessPosition target){
        ChessPiece capturedPiece = this.getPiece(target);
        switch(capturedPiece.getTeamColor()){ //list.set(list.indexOf(oldObject), newObject);
            case WHITE:
                this.whitePieces.remove(target);
                break;
            case BLACK:
                this.blackPieces.remove(target);
                break;
        }
    }

    // Finds the endpoints of each teams' possible moves to check for check.
    public Collection<ChessPosition> whiteMoveEnds (ChessBoard board){
        ArrayList<ChessPosition> whitePieces = board.getWhitePieces();
        HashSet<ChessPosition> endPositions = new HashSet<>();
        HashSet<ChessMove> whiteMoves = new HashSet<>();
        whitePieces.forEach(position -> whiteMoves.addAll(board.getPiece(position).pieceMoves(board, position)));
        whiteMoves.forEach(move -> endPositions.add(move.getEndPosition()));
        return endPositions;
    }

    public Collection<ChessPosition> blackMoveEnds (ChessBoard board){
        ArrayList<ChessPosition> blackPieces = board.getBlackPieces();
        HashSet<ChessPosition> endPositions = new HashSet<>();
        HashSet<ChessMove> blackMoves = new HashSet<>();
        blackPieces.forEach(position -> blackMoves.addAll(board.getPiece(position).pieceMoves(board, position)));
        blackMoves.forEach(move -> endPositions.add(move.getEndPosition()));
        return endPositions;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.spaces[position.getRow()][position.getColumn()] = piece;

        // Logic for storing the pieces.  TODO The chessgame will store the endpoints of the valid moves to calculate check and stuff
        switch(piece.getTeamColor()) {
            case WHITE:
                this.whitePieces.add(position);
                break;
            case BLACK:
                this.blackPieces.add(position);
                break;
        }
    }

    public void removePiece(ChessPosition position){
        this.spaces[position.getRow()][position.getColumn()] = null;
    }



    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.spaces[position.getRow()][position.getColumn()];
    }

    public boolean occupied(ChessPosition target) { // Replaces position.occupied value
        return this.spaces[target.getRow()][target.getColumn()] != null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.spaces = new ChessPiece[9][9];
        // R N B Q K B N R

        //Set white pawns;
        for (int i = 1; i <=8; i++){
            this.addPiece(new ChessPosition(2,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        //Set black pawns
        for (int i = 1; i <=8; i++){
            this.addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        ChessPiece[] whitePieces = new ChessPiece[8];
        whitePieces[0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        whitePieces[1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        whitePieces[2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        whitePieces[3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        whitePieces[4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        whitePieces[5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        whitePieces[6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        whitePieces[7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        ChessPiece[] blackPieces = new ChessPiece[8];
        blackPieces[0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        blackPieces[1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        blackPieces[2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        blackPieces[3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        blackPieces[4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        blackPieces[5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        blackPieces[6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        blackPieces[7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        //Set white pieces
        for (int i = 1; i <=8; i++){
            this.addPiece(new ChessPosition(1,i), whitePieces[i-1]);
        }
        //Set black pawns
        for (int i = 1; i <=8; i++){
            this.addPiece(new ChessPosition(8,i), blackPieces[i-1]);
        }
    }

    public Collection<ChessMove> getPieceMoves(ChessPosition myPosition) {
        return this.getPiece(myPosition).pieceMoves(this, myPosition);
    }


    @Override
    public String toString() {
        String output = "CHESSBOARD:\n";
        for (int y = 8; y > 0; y--) {
            for (int x = 1; x <=8; x++){
                if(spaces[y][x] == null){
                    output += "[ ]";
                }
                else{
                    output +=  "[" + (getPiece(new ChessPosition(y, x)).toString()) + "]";
                }
            }
            output += "\n";
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(spaces, that.spaces);
    }



    @Override
    public int hashCode() {
        return Arrays.deepHashCode(spaces);
    }
}
