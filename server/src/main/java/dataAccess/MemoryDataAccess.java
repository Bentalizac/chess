package dataAccess;

import java.util.Collection;
import java.util.HashMap;

import model.UserData;
import model.AuthData;
import model.GameData;


public class MemoryDataAccess {

    private int nextId = 1;
    final private HashMap<Integer, UserData> userData = new HashMap<>();
    final private HashMap<Integer, AuthData> authData = new HashMap<>();
    final private HashMap<Integer, GameData> gameData = new HashMap<>();

    public void clearAllData() {
        userData.clear();
        authData.clear();
        gameData.clear();
    }

}
