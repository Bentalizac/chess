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

    public AuthData createUser(UserData data) throws ResponseException{
        UserData existingData = dataAccess.getUser(data.userName());
        if(existingData == null){
            dataAccess.createUser(data);
            String authToken = UUID.randomUUID().toString();
            AuthData newAuthData = dataAccess.createAuth(data.userName(), authToken);
            return newAuthData;
        }
        else throw new ResponseException(300, "USER ALREADY EXISTS");
    }
    public ArrayList<UserData> getUsers() {
        return dataAccess.getAllUsers();
    }
    public void deleteAllData() throws ResponseException {
        this.dataAccess.clearData();
    }

    public AuthData login(String username, String password) {
        return null;
    }

    public void logout(String authToken) {
        UserData data = dataAccess.getUserByAuth(authToken);
    }

}
