package webSocketMessages.userCommands;

import model.AuthData;

public class SpectateCommand extends UserGameCommand{


    public SpectateCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

}
