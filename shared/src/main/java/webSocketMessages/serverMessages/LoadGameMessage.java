package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends Notification{

    private final String game;
    private final ChessGame.TeamColor color;

    public LoadGameMessage(ServerMessageType type, GameData serializedGame, ChessGame.TeamColor activeColor) {
        super("Loading " + serializedGame.gameName() + "\n");
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game =  new Gson().toJson(serializedGame.game(), ChessGame.class);
        this.color = activeColor;
    }

    public String getGame(){
        return game;
    }

    public String toString(){
        return new Gson().toJson(this, LoadGameMessage.class);
    }
    public ChessGame.TeamColor getColor() {
        return color;
    }

}
