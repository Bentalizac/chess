package service;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class GameService {

    // This class touches nothing in the ChessGame game field of the gameData record class. That may need to be changed later
    private final MemoryDataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private AuthData isAuthorized(String authToken) {
        return dataAccess.getUserByAuth(authToken);
    }

    private GameData buildNewGame(String gameName) { // Builds blank GameData records
        int gameID = dataAccess.getNextID();
        return new GameData(gameID, null, null, gameName, null);
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        AuthData authorization = isAuthorized(authToken);
        if (authorization == null || authorization.authToken() == null) {
            throw new ResponseException(401, "error: TOKEN NOT AUTHORIZED");
        }
        GameData game = this.buildNewGame(gameName);
        dataAccess.createGame(game);
        return game.gameID();
    }

    private ArrayList<GameData> getGames() {
        return dataAccess.getGames();
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        AuthData authorization = isAuthorized(authToken);
        if (authorization == null || authorization.authToken() == null) {
            throw new ResponseException(401, "error: TOKEN NOT AUTHORIZED");
        }
        return this.getGames();
    }
}
