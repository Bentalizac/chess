package webSocketMessages.notifications;


import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
