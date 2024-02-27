package dataAccess;

import java.util.ArrayList;
import java.util.HashMap;


import model.UserData;
import model.AuthData;
import model.GameData;


public class MemoryDataAccess implements DataAccess{

    private int nextGameId = 1;
    final private HashMap<String, UserData> userData = new HashMap<>(); // Stores userData hashed using the username
    final private HashMap<String, AuthData> authData = new HashMap<>();
    final private HashMap<Integer, GameData> gameData = new HashMap<>();

    public void createUser(UserData user) {
        userData.put(user.username(), user);
    }

    public int getNextID() {
        return this.nextGameId;
    }

    public AuthData login(String username, String authToken){
        var data = new AuthData(authToken, username);
        authData.put(authToken, data);
        return data;
    }

    public UserData getUser(String username) {return userData.get(username);}

    public ArrayList<UserData> getAllUsers() {
        ArrayList<UserData> output = new ArrayList<>();
        for (String key: userData.keySet()) {
            output.add(userData.get(key));
        }
        return output;
    }

    public AuthData getUserByAuth(String authtoken) {
        return authData.get(authtoken);
    }


    public void logout(AuthData data) {
        authData.remove(data.authToken());
    }

    public void clearData() {
        userData.clear();
        authData.clear();
        gameData.clear();
    }

    //              GAME ACCESS METHODS

    public ArrayList<GameData> getGames() {
        ArrayList<GameData> output = new ArrayList<>();
        for (int key: gameData.keySet()) {
            output.add(gameData.get(key));
        }
        return output;
    }

    public GameData getGame(int gameID){
        return gameData.get(gameID);
    }

    public void createGame(GameData newGame) {
        gameData.put(newGame.gameID(), newGame);
        this.nextGameId += 1;
    }

    public void updateGame(GameData game) {
        gameData.put(game.gameID(),game);
    }

}
