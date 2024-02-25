package dataAccess;

import java.util.Collection;
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
        userData.put(user.userName(), user);
    }

    public AuthData createAuth(String username, String authToken){
        var data = new AuthData(authToken, username);
        authData.put(username, data);
        return data;
    }

    public UserData getUser(String username) {return userData.get(username);}
    public AuthData getAuth(String username) {return authData.get(username);}

    public void clearData() {
        userData.clear();
        authData.clear();
        gameData.clear();
    }

}
