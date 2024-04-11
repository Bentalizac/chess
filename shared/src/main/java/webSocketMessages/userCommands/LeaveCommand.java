package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{

    public LeaveCommand(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
