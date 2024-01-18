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
        ChessPosition start = myPosition;
        HashSet<ChessMove> validMoves = new HashSet<>();
        System.out.println(board);
        switch (piece.type) {
            case KING:
                break;
            case PAWN:
                break;
            case ROOK:
                break;
            case QUEEN:
                break;
            case BISHOP:
                validMoves.addAll(diagonalMovement(board, myPosition));
                System.out.println(validMoves);
                break;
            case KNIGHT:
                break;
            case null:
                return null;
        }
    return validMoves;
    }


    private Collection<ChessMove> diagonalMovement(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        // Up/right
        validMoves.addAll(modularDiagonal(myPosition, board, 1, 1));
        // down/right
        validMoves.addAll(modularDiagonal(myPosition, board, 1, -1));
        // Up/left
        validMoves.addAll(modularDiagonal(myPosition, board, -1, 1));
        // down/left
        validMoves.addAll(modularDiagonal(myPosition, board, -1, -1));

        /**
         // Down/left
        System.out.println("Checking down/left");
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(!board.spaces[y-1][x-1].isEdge()){ // Doesn't account for capturing the first enemy piece blocking the path
            System.out.println("Space at " + x + "/" + y + " is a potential move" );
            if (board.spaces[y-1][x-1].occupied) {
                if (isEnemy(myPosition.getPiece().getTeamColor(), board.spaces[y-1][x-1])){
                    validMoves.add(new ChessMove(myPosition, board.spaces[y-1][x-1], myPosition.getPiece().getPieceType()));
                    break; // This should terminate after marking a capture-able tile as valid
                }}
            else{
                validMoves.add(new ChessMove(myPosition, board.spaces[y+1][x+1], this.getPieceType()));
            }
            x--;
            y--;}
        Old version of loop, kept for records
         * while(!board.spaces[y+1][x-1].isEdge() | !board.spaces[x+1][y+1].occupied){
         System.out.println("Space at " + x + "/" + y + " is a valid move" );
         }*/
        return validMoves;



    }

    private boolean isValid(ChessPosition target) {
        if (!target.occupied){
            return true;
        }
        else return isEnemy(target);
    }

    private Collection<ChessMove> modularDiagonal(ChessPosition myPosition, ChessBoard board,int xModifier, int yModifier) {
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        HashSet<ChessMove> validMoves = new HashSet<>();

        while (x+xModifier <= 8 && x+xModifier > 0 && y + yModifier <= 8 && y + yModifier >0) {
            x = x + xModifier;
            y = y + yModifier;
            ChessPosition target = board.spaces[x][y];
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
