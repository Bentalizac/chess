package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{

    public ResignCommand(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}
