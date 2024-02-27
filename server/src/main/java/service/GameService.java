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

    private final MemoryDataAccess dataAccess;

    public GameService() {dataAccess = new MemoryDataAccess();}

    private Boolean isAuthorized(String authToken) {
        AuthData dbToken = dataAccess.getUserByAuth(authToken);
        return dbToken != null && dbToken.authToken() != null;
    }

    public ArrayList<GameData> getGames() {
        return dataAccess.getGames();
    }

}
