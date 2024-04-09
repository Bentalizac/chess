package webSocketMessages.notifications;

import com.google.gson.Gson;

public record Action(Type type, String visitorName) {
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
