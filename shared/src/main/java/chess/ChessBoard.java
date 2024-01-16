package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition[][] spaces;
    public ChessBoard() {
        this.spaces = new ChessPosition[9][9];
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++) {
                this.spaces[i][j] = new ChessPosition(i, j);
            }
        }
        ChessPiece test = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING);
        //this.spaces[1][1].setPiece(test);

        this.addPiece(this.spaces[1][1], test);

        System.out.println(this.spaces[1][1]);
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.spaces[position.getRow()][position.getColumn()].setPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.spaces[position.getRow()][position.getColumn()].getPiece();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
