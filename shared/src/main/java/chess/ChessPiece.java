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
                //Up/Right Diagonal
                int x = start.getRow();
                int y = start.getColumn();
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
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        // Up/right
        while(x <= 8 && y <= 8) {
            ChessPosition target = board.spaces[x][y];
            x++; y++;
            System.out.println(target);
            if (!isEnemy(this.pieceColor, target)) {break;}
            if (isValid(target)){
                ChessMove newMove = new ChessMove(myPosition, target);
                validMoves.add(newMove);
                if (target.occupied) {break;}
            }
        }
        x = myPosition.getRow(); y = myPosition.getColumn();

        // down/right
        while(x <= 8 && y >= 1) {
            ChessPosition target = board.spaces[x][y];
            x++; y--;
            System.out.println(target);
            if (!isEnemy(this.pieceColor, target)) {break;}
            if (isValid(target)){
                ChessMove newMove = new ChessMove(myPosition, target);
                validMoves.add(newMove);
                if (target.occupied) {break;}
            }
        }
        x = myPosition.getRow(); y = myPosition.getColumn();

        // Up/left
        while(x >= 1 && y <= 8) {
            ChessPosition target = board.spaces[x][y];
            x--; y++;
            System.out.println(target);
            if (!isEnemy(this.pieceColor, target)) {break;}
            if (isValid(target)){
                ChessMove newMove = new ChessMove(myPosition, target);
                validMoves.add(newMove);
                if (target.occupied) {break;}
            }
        }
        x = myPosition.getRow(); y = myPosition.getColumn();

        // down/left
        while(x >= 1 && y >= 1) {
            ChessPosition target = board.spaces[x][y];
            x--; y--;
            System.out.println(target);
            if (!isEnemy(this.pieceColor, target)) {break;}
            if (isValid(target)){
                ChessMove newMove = new ChessMove(myPosition, target);
                validMoves.add(newMove);
                if (target.occupied) {break;}
            }
        }
        x = myPosition.getRow(); y = myPosition.getColumn();

        System.out.println(validMoves);


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
        else return isEnemy(this.getTeamColor(), target);
    }

    private boolean isEnemy(ChessGame.TeamColor ally, ChessPosition target) { // Checks if a piece at a position is an enemy
        return target.getPiece().getTeamColor() != ally;
    }

    private Collection<ChessPosition> getThreats(Collection<ChessMove> validMoves) {
        Collection<ChessPosition> threatenedTiles = new HashSet<>();
        for (ChessMove tile : validMoves){
            if (tile.getEndPosition().occupied && isEnemy(this.pieceColor,tile.getEndPosition())){
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
