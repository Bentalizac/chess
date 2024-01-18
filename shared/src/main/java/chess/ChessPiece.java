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
    private PieceType type;
    private ChessGame.TeamColor pieceColor;

    private Collection<ChessPosition> threats;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.type = type;
        this .pieceColor = pieceColor;
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
        System.out.println(board);
        switch (piece.type) {
            case KING:
                validMoves.addAll(kingMovement(board, myPosition));
                break;
            case PAWN:
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
        validMoves.addAll(linearMovement(myPosition, board, 1, 1));
        // down/right
        validMoves.addAll(linearMovement(myPosition, board, 1, -1)); // This isn't working. the iteration of the x and y values somehow makes the target tile the piece's current tile
        // Up/left
        validMoves.addAll(linearMovement(myPosition, board, -1, 1));
        // down/left
        validMoves.addAll(linearMovement(myPosition, board, -1, -1));
        return validMoves;
    }

    private Collection<ChessMove> cardinalMovement(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        // Up
        validMoves.addAll(linearMovement(myPosition, board, 0, 1));
        // down
        validMoves.addAll(linearMovement(myPosition, board, 0, -1)); // This isn't working. the iteration of the x and y values somehow makes the target tile the piece's current tile
        // left
        validMoves.addAll(linearMovement(myPosition, board, -1, 0));
        // right
        validMoves.addAll(linearMovement(myPosition, board, 1, 0));
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

    //private Collection<ChessMove> pawnMovement

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
        if (validCoords(y, x)) {
            if (isValid(board.spaces[y][x])) {
                return new ChessMove(myPosition, board.spaces[y][x]);
            }
        }
        return null;
    }

    private Boolean validCoords(int y, int x){
        return x <= 8 && x>0 && y <=8 && y> 0;
    }

    private boolean isValid(ChessPosition target) {
        if (!target.occupied){
            return true;
        }
        else return isEnemy(target);
    }



    private Collection<ChessMove> linearMovement(ChessPosition myPosition, ChessBoard board, int xModifier, int yModifier) {
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        HashSet<ChessMove> validMoves = new HashSet<>();
        while (x+xModifier <= 8 && x+xModifier > 0 && y + yModifier <= 8 && y + yModifier >0) {
            x = x + xModifier;
            y = y + yModifier;
            ChessPosition target = board.spaces[y][x];
            ChessMove newMove = new ChessMove(myPosition, target);
            if (!target.occupied) {
                validMoves.add(newMove);
            }
            else {
                if (isEnemy((target))){
                    validMoves.add(newMove);
                }
                break;
            }
        }
        return validMoves;
    }

    private boolean isEnemy(ChessPosition target) { // Checks if a piece at a position is an enemy
        return target.getPiece().getTeamColor() != this.getTeamColor();
    }

    private Collection<ChessPosition> getThreats(Collection<ChessMove> validMoves) {
        Collection<ChessPosition> threatenedTiles = new HashSet<>();
        for (ChessMove tile : validMoves){
            if (tile.getEndPosition().occupied && isEnemy(tile.getEndPosition())){
                threatenedTiles.add(tile.getEndPosition());
            }
        }
        return threatenedTiles;
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
        return "ChessPiece{" +
                "type='" + type + '\'' +
                ", pieceColor='" + pieceColor + '\'' +
                '}';
    }
}
