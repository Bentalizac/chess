package webSocketMessages.notifications;

import com.google.gson.Gson;
import model.AuthData;

public record Action(Type type, AuthData auth) {
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
