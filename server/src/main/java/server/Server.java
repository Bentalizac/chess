package server;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import com.google.gson.Gson;

import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;
    // TODO Add auth and game services next

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        // There's a line about websockets next
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::deleteAllData);
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

        Spark.exception(ResponseException.class, this::exceptionHandler);


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

    private void exceptionHandler(ResponseException ex, Request req, Response res) { // Yoinked from petshop
        res.status(ex.StatusCode());
    }

    private String exceptionToString(ResponseException ex) {
        return "{ \"message\": \"" + ex.getMessage() + "\" }";
    } //Generates the JSON string that needs to be passed back from an exception

    public Object addUser(Request req, Response res) throws ResponseException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData userAuth = null;
        try {
            userAuth = userService.createUser(user);
            res.status(200);
            return new Gson().toJson(userAuth);
        }
        catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }

    public Object deleteAllData(Request req, Response res) throws ResponseException {
        userService.deleteAllData();
        res.status(200);
        return "";
    }

    public Object login(Request req, Response res) throws ResponseException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        System.out.println(user.username());
        AuthData authData = null;
        try {
            authData = userService.login(user.username(), user.password());
            res.status(200);
            //res.body(new Gson().toJson(authData));
            return new Gson().toJson(authData);
        } catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }

    public Object logout(Request req, Response res) {
        String userToken = req.headers("authorization");
        try{
            userService.logout(userToken);
            res.status(200);
            return "{}";
        }
        catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }


    //              GAME METHODS

    public Object listGames(Request req, Response res) {
        return null;
    }


}
