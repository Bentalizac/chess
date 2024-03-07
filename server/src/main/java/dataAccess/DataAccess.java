package dataAccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;


public interface DataAccess {

    void clearData() throws ResponseException;

    UserData getUser(String userName) throws ResponseException;

    ArrayList<UserData> getAllUsers() throws ResponseException;

    AuthData login(String username, String authToken) throws ResponseException;

    AuthData getUserByAuth(String authtoken) throws ResponseException;

    void createUser(UserData user) throws ResponseException;

    void logout(AuthData data);

    ArrayList<GameData> getGames();

    int getNextID();

    void createGame(GameData game) throws ResponseException;

    GameData getGame(int gameID) throws ResponseException;

    void updateGame(GameData game);



}
