package dataAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

    public AuthData login(String username, String authToken){
        var data = new AuthData(authToken, username);
        authData.put(username, data);
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
        for (String key: authData.keySet()) {
            AuthData datum = authData.get(key);
            if(datum == null) {
                return null;
            }
            if (Objects.equals(datum.authToken(), authtoken)) {
                return datum;
            }
        }
        return null;
    }

    public void logout(AuthData data) {
        authData.put(data.username(), null);
    }

    public void clearData() {
        userData.clear();
        authData.clear();
        gameData.clear();
    }

}
