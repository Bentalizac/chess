package server;

import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import com.google.gson.Gson;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        //var dataAccess = new MemoryDataAccess();
        var dataAccess = new MySQLDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

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

        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);

        Spark.exception(ResponseException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) { // Yoinked from petshop
        res.status(ex.statusCode());
    }

    private String exceptionToString(ResponseException ex) {
        return "{ \"message\": \"" + ex.getMessage() + "\" }";
    } //Generates the JSON string that needs to be passed back from an exception

    private String encryptPassword(String rawPassword) {
        if(rawPassword == null) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(rawPassword);
    }

    private UserData encryptPassword(UserData input){
        return new UserData(input.username(), encryptPassword(input.password()), input.email());
    }

    public Object addUser(Request req, Response res) {
        UserData user = encryptPassword(new Gson().fromJson(req.body(), UserData.class));
        AuthData userAuth;
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

    public Object deleteAllData(Request req, Response res) {
        userService.deleteAllData();
        res.status(200);
        return "";
    }

    public Object login(Request req, Response res) {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData;
        try {
            authData = userService.login(user.username(), user.password());
            res.status(200);

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
        String userToken = req.headers("authorization");
        try {
            var games = gameService.listGames(userToken);
            res.status(200);
            return new Gson().toJson(Map.of("games", games));
        }
        catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }

    public Object createGame(Request req, Response res) {
        String userToken = req.headers("authorization");
        GameData data = new Gson().fromJson(req.body(), GameData.class);

        try {
            int gameID = gameService.createGame(data.gameName(), userToken);
            res.status(200);
            return "{ \"gameID\": \"" + gameID + "\" }";
        }
        catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }

    public Object joinGame(Request req, Response res) {
        String userToken = req.headers("authorization");
        JoinGameRequest data = new Gson().fromJson(req.body(), JoinGameRequest.class);

        try{
            gameService.joinGame(data, userToken);
            res.status(200);
            return "{}";
        }
        catch (ResponseException ex) {
            exceptionHandler(ex, req, res);
            return exceptionToString(ex);
        }
    }


}
