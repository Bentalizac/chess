package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private ChessPiece piece;
    public boolean occupied = false;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return occupied == that.occupied && row == that.row && col == that.col && Objects.equals(piece, that.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, occupied, row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "piece=" + piece +
                ", occupied=" + occupied +
                ", row=" + row +
                ", col=" + col +
                '}';
    }

    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() { return this.row;
    }
    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }
    public void setPiece(ChessPiece piece) {this.occupied = true;this.piece = piece;} /** TODO make sure the occupied value is set to false when a piece moves off*/
    public ChessPiece getPiece() {return this.piece;}

    public boolean isEdge() {
        return this.row + 1 >= 8 | this.row - 1 <= 0 | this.col + 1 >= 8 | this.col - 1 <= 0;
    }
}

