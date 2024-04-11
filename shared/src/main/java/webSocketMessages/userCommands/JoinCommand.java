package webSocketMessages.userCommands;

import chess.ChessGame;
import model.AuthData;

public class JoinCommand extends UserGameCommand{

    public JoinCommand(String authToken, int gameID ,ChessGame.TeamColor playerColor) {
        super(authToken);
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
    public ChessGame.TeamColor getColor() {return playerColor;}
    public int gameID() {return this.gameID;}


}
