package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor pieceColor;

    private char strRep; // do not include in equals or hash
    private int endRow; // This only matters on a pawn, do not include in equals or hash

    //private Collection<ChessPosition> threats;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;

        switch (this.type){ // debugging/toString, not needed for functionality
            case KNIGHT:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'N': 'n');
                break;
            case BISHOP:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'B': 'b');
                break;
            case QUEEN:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'Q': 'q');
                break;
            case KING:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'K': 'k');
                break;
            case ROOK:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'R': 'r');
                break;
            case PAWN:
                this.strRep = ((pieceColor == ChessGame.TeamColor.WHITE) ? 'P': 'p');
                this.endRow = ((pieceColor == ChessGame.TeamColor.WHITE) ? 8: 1);
                break;
            case null:
                break;
        }

    }
    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = this;
        HashSet<ChessMove> validMoves = new HashSet<>();
        switch (piece.type) {
            case KING:
                validMoves.addAll(kingMovement(board, myPosition));
                break;
            case PAWN:
                validMoves.addAll(pawnMovement(board, myPosition));
                break;
            case ROOK:
                validMoves.addAll(cardinalMovement(board, myPosition));
                break;
            case QUEEN:
                validMoves.addAll(cardinalMovement(board, myPosition));
                validMoves.addAll(diagonalMovement(board, myPosition));
                break;
            case BISHOP:
                validMoves.addAll(diagonalMovement(board, myPosition));
                break;
            case KNIGHT:
                validMoves.addAll(knightMovement(board, myPosition));
                break;
            case null:
                return null;
        }
        validMoves.remove(null);
    return validMoves;
    }

    private Collection<ChessMove> diagonalMovement(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        // Up/right
        validMoves.addAll(linearMovement(board, myPosition, 1, 1));
        // down/right
        validMoves.addAll(linearMovement(board, myPosition, 1, -1));
        // Up/left
        validMoves.addAll(linearMovement(board, myPosition, -1, 1));
        // down/left
        validMoves.addAll(linearMovement(board, myPosition, -1, -1));
        return validMoves;
    }

    private Collection<ChessMove> cardinalMovement(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        // Up
        validMoves.addAll(linearMovement(board, myPosition, 0, 1));
        // down
        validMoves.addAll(linearMovement(board, myPosition, 0, -1));
        // left
        validMoves.addAll(linearMovement(board, myPosition, -1, 0));
        // right
        validMoves.addAll(linearMovement(board, myPosition, 1, 0));
        return validMoves;
    }

    private Collection<ChessMove> knightMovement(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        validMoves.add(moveTo(board, myPosition, 2, 1));
        validMoves.add(moveTo(board, myPosition, 2, -1));
        validMoves.add(moveTo(board, myPosition, -2, 1));
        validMoves.add(moveTo(board, myPosition, -2, -1));
        validMoves.add(moveTo(board, myPosition, 1, 2));
        validMoves.add(moveTo(board, myPosition, -1, 2));
        validMoves.add(moveTo(board, myPosition, 1, -2));
        validMoves.add(moveTo(board, myPosition, -1, -2));
        return validMoves;
    }

    private Collection<ChessMove> pawnMovement(ChessBoard board, ChessPosition myPosition) {

        HashSet<ChessMove> validMoves = new HashSet<>();
        int direction = ((this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1);
        int x = myPosition.getColumn();
        int y = myPosition.getRow();

        ChessPosition fwdTarget = new ChessPosition(y + direction, x);
        if (!board.occupied(fwdTarget)) {
            validMoves.addAll(promotionMoves(myPosition, fwdTarget));
            if ((this.pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) | (this.pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)) { // Double movement arbitrator line
                ChessPosition dblTarget = new ChessPosition(y + (2*direction), x);
                if (!board.occupied(dblTarget)) {validMoves.addAll(promotionMoves(myPosition, dblTarget)); } // Nested if to check for initial double movement
            }
        }
        //Diagonal Movement
        if(validCoords(y+direction, x-1)) {
            ChessPosition westTarget = new ChessPosition(y + direction, x - 1);
            if (board.occupied((westTarget)) && isEnemy(board.getPiece(westTarget))) {
                validMoves.addAll(promotionMoves(myPosition, westTarget));
            }
        }
        if(validCoords(y+direction, x+1)) {
            ChessPosition eastTarget = new ChessPosition(y + direction, x + 1);
            if (board.occupied((eastTarget)) && isEnemy(board.getPiece(eastTarget))) {
                validMoves.addAll(promotionMoves(myPosition, eastTarget));
            }
        }

        return validMoves;
    }

    private Collection<ChessMove> promotionMoves(ChessPosition myPosition, ChessPosition target) {
        HashSet<ChessMove> validMoves = new HashSet<>();

        if (target.getRow() != this.endRow) {
            validMoves.add(new ChessMove(myPosition, target));
            return validMoves;
        }
        validMoves.add(new ChessMove(myPosition, target, PieceType.ROOK));
        validMoves.add(new ChessMove(myPosition, target, PieceType.KNIGHT));
        validMoves.add(new ChessMove(myPosition, target, PieceType.QUEEN));
        validMoves.add(new ChessMove(myPosition, target, PieceType.BISHOP));
        return validMoves;
    }

    private Collection<ChessMove> kingMovement(ChessBoard board, ChessPosition myPosition) { // TODO implement check restrictions after implementing threatened tiles
        HashSet<ChessMove> validMoves = new HashSet<>();
        validMoves.add(moveTo(board, myPosition, 1, 1));
        validMoves.add(moveTo(board, myPosition, 1, -1));
        validMoves.add(moveTo(board, myPosition, -1, 1));
        validMoves.add(moveTo(board, myPosition, -1, -1));
        validMoves.add(moveTo(board, myPosition, 1, 0));
        validMoves.add(moveTo(board, myPosition, -1, 0));
        validMoves.add(moveTo(board, myPosition, 0, 1));
        validMoves.add(moveTo(board, myPosition, 0, -1));
        return validMoves;
    }

    private ChessMove moveTo(ChessBoard board, ChessPosition myPosition, int yMod, int xMod) {
        int y = myPosition.getRow() + yMod;
        int x = myPosition.getColumn() + xMod;
        ChessPosition target = new ChessPosition(y, x);
        if (validCoords(y, x)) {
            if (isValid(board, target)) {
                return new ChessMove(myPosition, target);
            }
        }
        return null;
    }

    private Boolean validCoords(int y, int x){
        return x <= 8 && x>0 && y <=8 && y> 0;
    }

    private boolean isValid(ChessBoard board, ChessPosition target) {
        if (!board.occupied(target)){
            return true;
        }
        else return isEnemy(board.getPiece(target));
    }

    private Collection<ChessMove> linearMovement(ChessBoard board, ChessPosition myPosition, int xModifier, int yModifier) {
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        HashSet<ChessMove> validMoves = new HashSet<>();
        while (x+xModifier <= 8 && x+xModifier > 0 && y + yModifier <= 8 && y + yModifier >0) {
            x = x + xModifier;
            y = y + yModifier;
            ChessPosition target = new ChessPosition(y, x);
            ChessMove newMove = new ChessMove(myPosition, target);
            if (!board.occupied(target)) {
                validMoves.add(newMove);
            }
            else {
                if (isEnemy((board.getPiece(target)))){
                    validMoves.add(newMove);
                }
                break;
            }
        }
        return validMoves;
    }

    private boolean isEnemy(ChessPiece target) { return target.getTeamColor() != this.getTeamColor();}

    ChessPiece copy(){
        return new ChessPiece(this.pieceColor, this.getPieceType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return Objects.equals(type, that.type) && Objects.equals(pieceColor, that.pieceColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    @Override
    public String toString() {
        return String.valueOf(strRep);
    }
}
