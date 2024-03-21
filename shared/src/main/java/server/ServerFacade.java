package server;

import model.UserData;

public class ServerFacade {
    //private final String serverUrl;
    private final int serverPort;
    private int statusCode;



    public ServerFacade(int port) {
        serverPort = port;
    }

    public UserData register(UserData data) {
        var path = "/user";
        return null;
    }

}
