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
            AuthData newAuthData = dataAccess.login(data.userName(), authToken);
            return newAuthData;
        }
        else throw new ResponseException(409, "USER ALREADY EXISTS");
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

    public void logout(String authToken) throws ResponseException{
        AuthData data = dataAccess.getUserByAuth(authToken);
        if(data == null) {
            throw new ResponseException(404, "USER NOT FOUND");
        }
        else if(data.authToken() == null) {
            throw new ResponseException(403, "USER NOT LOGGED IN");
        }
        dataAccess.logout(data);
    }

}
