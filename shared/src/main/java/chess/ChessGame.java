package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ArrayList<ChessPiece[][]> gameHistory;
    private ChessBoard currentState;

    private TeamColor currentPlayer;
    public ChessGame() {
        this.setBoard(new ChessBoard());
        this.setTeamTurn(TeamColor.WHITE);
        this.gameHistory = new ArrayList<>();
        this.updateHistory();


    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentPlayer;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentPlayer = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return this.getBoard().getPieceMoves(startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */

    private boolean validateMove(ChessMove move){
        ChessPiece piece = this.currentState.getPiece(move.getStartPosition());

        if (!this.futureSight(move)){ // Checks if move will leave own team in check.
            return false;
        }
        return true;
    }

    private void movePiece(ChessMove move, ChessPiece piece){
        this.currentState.removePiece(move.getStartPosition());
        //Maybe TODO add flag if this was a capture move? Is that needed? If so, this is the spot for it.
        if(this.currentState.occupied(move.getEndPosition())){
            this.currentState.removePiece(move.getEndPosition());
        }
        this.currentState.addPiece(move.getEndPosition(), piece);
        switch(this.getTeamTurn()){ //list.set(list.indexOf(oldObject), newObject);
            case WHITE:
                ;
                break;
            case BLACK:
                ;
                break;
        }
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = this.getBoard().getPiece(move.getStartPosition());

        if (piece.getTeamColor() != this.currentPlayer | !piece.pieceMoves(this.currentState, move.getStartPosition()).contains(move)) {// TODO add checkTest
            throw new InvalidMoveException();
        }
        this.currentState.updateArrays(move, piece);
        this.movePiece(move, piece);
        this.updateHistory();
        this.changeTurn();
    }

    private void changeTurn(){
        switch(this.getTeamTurn()){
            case WHITE:
                this.setTeamTurn(TeamColor.BLACK);
                break;
            case BLACK:
                this.setTeamTurn(TeamColor.WHITE);
                break;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public ArrayList<ChessPosition> getThreatenedTiles(TeamColor teamColor) {
        ArrayList<ChessPosition> threatenedTiles = new ArrayList<>();
        switch (teamColor){
            case WHITE:
                threatenedTiles.addAll(this.currentState.blackMoveEnds(this.currentState));
                break;
            case BLACK:
                threatenedTiles.addAll(this.currentState.whiteMoveEnds(this.currentState));
                break;
        }
        return threatenedTiles;
    }

    public ArrayList<ChessPiece> getThreatenedPieces(ArrayList<ChessPosition> threatenedTiles, TeamColor teamColor){
        ArrayList<ChessPiece> threatenedPieces = new ArrayList<>();
        threatenedTiles.forEach(tile -> threatenedPieces.add(this.currentState.getPiece(tile))); //This doesn't threaten friendly pieces because those moves were never added to validMoves
        threatenedPieces.removeIf(Objects::isNull);
        return threatenedPieces;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ArrayList<ChessPosition> threatenedTiles = new ArrayList<>();
        ArrayList<ChessPiece> threatenedPieces = new ArrayList<>();
        threatenedTiles = getThreatenedTiles(teamColor);
        threatenedPieces = getThreatenedPieces(threatenedTiles, teamColor);
        for (ChessPiece piece : threatenedPieces) {
            if( piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                return true;
            }
        }
        return false;
    }

    private void updateHistory(){
        var spaces = this.getBoard().getSpaces();
        this.gameHistory.add(spaces);
    }
    private boolean futureSight(ChessMove move) { // Generates the board state a move would create, and returns true if the move is valid and will not leave the current team in check


        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentState = board;
        System.out.println(this);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.currentState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(currentState, chessGame.currentState) && currentPlayer == chessGame.currentPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentState, currentPlayer);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentState=" + currentState +
                ", currentPlayer=" + currentPlayer +
                "}";
    }
}
