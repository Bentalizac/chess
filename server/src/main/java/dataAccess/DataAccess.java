package dataAccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;


public interface DataAccess {

    void clearData() throws ResponseException;

    UserData getUser(String userName) throws ResponseException;

    ArrayList<UserData> getAllUsers();

    AuthData createAuth(String username, String authToken);

    AuthData getUserByAuth(String authtoken) throws ResponseException;

    void createUser(UserData user);




}
