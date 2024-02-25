package service;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;


public class UserService implements ChessService{

    private final MemoryDataAccess dataAccess;

    public UserService(){
        dataAccess = new MemoryDataAccess();
    }

    public AuthData createUser(UserData data) {
        UserData existingData = dataAccess.getUser(data.userName());
        if(existingData == null){
            dataAccess.createUser(data);
            String authToken = UUID.randomUUID().toString();
            AuthData newAuthData = dataAccess.createAuth(data.userName(), authToken);
            return newAuthData;
        }
        else{
            return dataAccess.getAuth(data.userName());
        }
    }
    public ArrayList<UserData> getUsers() {
        return dataAccess.getAllUsers();
    }
    public void deleteAllData() throws ResponseException {
        this.dataAccess.clearData();
    }
}
