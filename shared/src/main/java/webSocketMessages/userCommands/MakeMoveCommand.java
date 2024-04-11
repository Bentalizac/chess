package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMoveCommand extends UserGameCommand{

    public MakeMoveCommand(String authToken, ChessMove move, int gameID) {
        super(authToken);
        this.move = move;
        this.gameID = gameID;
    }



}
