package service;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class UserService{

    private final MemoryDataAccess dataAccess;



    public UserService(MemoryDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser(UserData data) throws ResponseException{
        UserData existingData = dataAccess.getUser(data.username());

        if(existingData == null){
            if(data.password() == null || data.email() == null || data.username() == null) {
                throw new ResponseException(400, "error: INPUT FIELD MISSING");
            }
            dataAccess.createUser(data);
            String authToken = this.generatePassword();
            AuthData newAuthData = dataAccess.login(data.username(), authToken);
            return newAuthData;
        }
        else throw new ResponseException(403, "error: USER ALREADY EXISTS");
    }
    public ArrayList<UserData> getUsers() {
        return dataAccess.getAllUsers();
    }
    public void deleteAllData() throws ResponseException {
        this.dataAccess.clearData();
    }

    public AuthData login(String username, String password) throws ResponseException{
        UserData user = dataAccess.getUser(username);
        if(user == null) {
            throw new ResponseException(401, "error: USER NOT FOUND");
        }
        else if (!Objects.equals(user.password(), password)) {
            throw new ResponseException(401, "error: INCORRECT PASSWORD");
        }
        return dataAccess.login(username, this.generatePassword());
    }

    public void logout(String authToken) throws ResponseException{
        AuthData data = dataAccess.getUserByAuth(authToken);
        if(data == null) {
            throw new ResponseException(401, "error: USER NOT FOUND");
        }
        else if(data.authToken() == null) {
            throw new ResponseException(500, "error: USER NOT LOGGED IN");
        }
        dataAccess.logout(data);
    }

}
