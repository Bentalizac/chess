package server;

import dataAccess.DataAccess;
import exception.ResponseException;
import service.ChessService;
import model.UserData;
import model.AuthData;
import model.GameData;

import spark.*;

import javax.xml.crypto.Data;
import java.util.Map;

public class Server {
    private final ChessService service;

    public Server(DataAccess dataAccess) {
        service = new ChessService(dataAccess);
        // There's a line about websockets next
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object deleteAllData(Request req, Response res) throws ResponseException {
        service.deleteAllData();
        res.status(204);
        return "";
    }

}
