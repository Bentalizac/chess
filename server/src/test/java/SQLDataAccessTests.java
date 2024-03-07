import dataAccess.DataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.UserData;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
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
        this.addUser();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        var resultUser = dataAccess.getUser(testUser.username());
        System.out.println(resultUser.toString());
    }

    @Test
    void getBadUser() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        assertNull(dataAccess.getUser("FakityFakerson"));
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
