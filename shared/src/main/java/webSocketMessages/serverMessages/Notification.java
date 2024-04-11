package webSocketMessages.serverMessages;

import model.GameData;

public class Notification extends ServerMessage{
    private final String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {return this.message;}
    public String getGame(){return null;};

}
