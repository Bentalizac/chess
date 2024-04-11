package webSocketMessages.serverMessages;

public class ErrorNotification extends Notification{
    private final String errorMessage;

    public ErrorNotification(String message) {
        super(message);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
