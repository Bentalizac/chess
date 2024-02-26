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
        System.out.println("SERVER SPOOLED UP");
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/data", this::deleteAllData);
        Spark.post("/addUser", this::addUser);


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
        res.status(204);
        return "";
    }

}
