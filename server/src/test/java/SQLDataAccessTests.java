import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.UserData;


import exception.ResponseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataAccessTests {

    private DataAccess getDataAccess() throws ResponseException {
        return new MySQLDataAccess();
    }
    @Test
    void addUser() throws ResponseException{
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        assertDoesNotThrow(()->dataAccess.createUser(testUser));
    }

    @Test
    void getUser() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanPablo", "password", "yee@haw.com");
        var resultUser = dataAccess.getUser(testUser.username());
        System.out.println(resultUser.toString());
    }

    @Test
    void getUsers() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        assertDoesNotThrow(dataAccess::getAllUsers);
        ArrayList<UserData> result = dataAccess.getAllUsers();
        System.out.println(result);
    }

    @Test
    void clearAll()throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        assertDoesNotThrow(dataAccess::clearData);
    }








}