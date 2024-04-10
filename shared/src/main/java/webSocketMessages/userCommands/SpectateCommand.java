package webSocketMessages.userCommands;

import model.AuthData;

public class SpectateCommand extends UserGameCommand{
    private final String username;
    public SpectateCommand(AuthData data) {
        super(data.authToken());
        this.username = data.username();
        this.commandType = CommandType.JOIN_OBSERVER;
    }
    public String getUsername() {
        return username;
    }
}
