package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class UserService{

    //private final MySQLDataAccess dataAccess;
    private final DataAccess dataAccess;

    public UserService(MemoryDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public UserService(MySQLDataAccess dataAccess) { this.dataAccess = dataAccess;}
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
            return dataAccess.login(data.username(), authToken);
        }
        else throw new ResponseException(403, "error: USER ALREADY EXISTS");
    }
    public ArrayList<UserData> getUsers() {
        try {
            return dataAccess.getAllUsers();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllData() {
        var thisIsToHaveUsageOfGetUsers = this.getUsers();
        try {
            this.dataAccess.clearData();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean passwordMatch(String raw, String hash) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return (Objects.equals(raw, hash)) || encoder.matches(raw, hash);
    }

    public AuthData login(String username, String password) throws ResponseException{
        UserData user = dataAccess.getUser(username);
        if(user == null) {
            throw new ResponseException(401, "error: USER NOT FOUND");
        }
        else if (!passwordMatch(password, user.password())) {
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
