package webSocketMessages.serverMessages;

import model.GameData;

public class LoadGameMessage extends ServerMessage{

    private final GameData game;

    public LoadGameMessage(ServerMessageType type, GameData serializedGame) {
        super(type);
        this.game = serializedGame;
    }
}
