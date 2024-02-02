package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ArrayList<ChessBoard> gameHistory;
    private ChessBoard currentState;

    private TeamColor currentPlayer;
    public ChessGame() {
        this.gameHistory = new ArrayList<>();
        this.setBoard(new ChessBoard());
        this.setTeamTurn(TeamColor.WHITE);
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
        HashSet<ChessMove> validMoves = (HashSet<ChessMove>) this.getBoard().getPieceMoves(startPosition);

        validMoves.removeIf(move -> !isValidMove(move)); // Iterates over generated validMoves and checks for check/team
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */

    private boolean isValidMove(ChessMove move){ // Primary purpose is basic validation and checking for check
        return futureSight(move, this.currentState.getPiece(move.getStartPosition()));
    }

    private void movePiece(ChessMove move, ChessPiece piece){
        this.currentState.removePiece(move.getStartPosition());
        if(this.currentState.occupied(move.getEndPosition())){
            this.currentState.capturePiece(move.getEndPosition());
            this.currentState.removePiece(move.getEndPosition());
        }
        this.currentState.addPiece(move.getEndPosition(), piece);

    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = this.getBoard().getPiece(move.getStartPosition());
        if (piece.getTeamColor() != this.currentPlayer | !validMoves(move.getStartPosition()).contains(move)) {// This test is potentially redundant, but only potentially.
            throw new InvalidMoveException();
        }
        this.currentState.updateArrays(move, piece);
        if(move.getPromotionPiece() != null){this.movePiece(move, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));}
        else{
        this.movePiece(move, piece);}
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
        this.gameHistory.add(new ChessBoard(this.currentState));
    }

    private void rollback(){ // This does not update the lists of pieces, and as such neither should any hypothetical moves
        this.currentState = new ChessBoard(this.gameHistory.get(gameHistory.size()-1)); // Make a new copy of the most recently saved game state
    }
    private boolean futureSight(ChessMove move, ChessPiece piece) { // Generates the board state a move would create, and returns true if the move is valid and will not leave the current team in check
        this.movePiece(move, piece);
        if(isInCheck(piece.getTeamColor())){
            this.rollback();
            return false;
        }
        this.rollback();
        return true;
    }

    private HashSet<ChessMove> getTeamMoves(TeamColor teamColor){
        var pieceList = new HashSet<ChessPosition>();
        var moves = new HashSet<ChessMove>();
        switch(teamColor){
            case WHITE:
                pieceList.addAll(this.currentState.getWhitePieces());
                break;
            case BLACK:
                pieceList.addAll(this.currentState.getBlackPieces());
                break;
        }
        for(ChessPosition position : pieceList){
            moves.addAll(this.validMoves(position));
        }
        return moves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var moves = new HashSet<ChessMove>();
        moves = getTeamMoves(teamColor);
        return moves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var moves = new HashSet<ChessMove>();
        moves = getTeamMoves(teamColor);
        return moves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentState = board;
        System.out.println(this);
        this.updateHistory();
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
