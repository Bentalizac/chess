package server;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.ChessService;
import com.google.gson.Gson;

import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final UserService userService;
    // TODO Add auth and game services next

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService();
        // There's a line about websockets next
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::deleteAllData);
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::login);



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    public int port() {
        return Spark.port();
    }

    public Object addUser(Request req, Response res) throws ResponseException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData userAuth = userService.createUser(user);
        return new Gson().toJson(user);

    }

    public Object deleteAllData(Request req, Response res) throws ResponseException {
        userService.deleteAllData();
        res.status(200);
        return "";
    }

    public Object login(Request req, Response res) throws ResponseException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        System.out.println(user.userName());
        try {
            AuthData authData = userService.login(user.userName(), user.password());
        }
        catch(ResponseException response) {
            if(response.StatusCode() == 401) {

            }
        }

        return null;
    }

}
