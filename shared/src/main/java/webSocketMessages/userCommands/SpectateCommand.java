package webSocketMessages.userCommands;

import model.AuthData;

public class SpectateCommand extends UserGameCommand{

    private final int gameID;

    public SpectateCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public int gameID() {return this.gameID;}
}
