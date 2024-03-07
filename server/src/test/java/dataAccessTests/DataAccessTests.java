package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private DataAccess getDataAccess() {
        return new MySQLDataAccess();
    }

    @BeforeEach
    void clearAll() {
        DataAccess dataAccess = getDataAccess();
        assertDoesNotThrow(dataAccess::clearData);
    }

    @Test
    void addUser() {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        assertDoesNotThrow(()->dataAccess.createUser(testUser));
    }

    @Test
    void addUserNoUsername() {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData(null, "password", "yee@haw.com");
        assertThrows(ResponseException.class, ()->dataAccess.createUser(testUser));
    }

    @Test
    void addUserNoPassword() {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", null, "yee@haw.com");
        assertThrows(ResponseException.class, ()->dataAccess.createUser(testUser));
    }

    @Test
    void addUserNoEmail() {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", null);
        assertThrows(ResponseException.class, ()->dataAccess.createUser(testUser));
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
    void login()  {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        assertDoesNotThrow(()->dataAccess.login(testUser.username(), testUser.password()));
    }

    @Test
    void logout() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        AuthData auth = dataAccess.login(testUser.username(),testUser.password());

        assertDoesNotThrow(()->dataAccess.logout(auth));
    }


    @Test
    void createGame() {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, null, null, "testing", new ChessGame());
        assertDoesNotThrow(()-> dataAccess.createGame(testGame));
    }



    @Test
    void watchGame() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, null, null, "testing", new ChessGame());
        dataAccess.createGame(testGame);
        assertDoesNotThrow(()-> dataAccess.updateGame(testGame));
    }

    @Test
    void watchGameBadId()  {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(1023434240, null, null, "testing", new ChessGame());
        assertThrows(ResponseException.class, ()-> dataAccess.updateGame(testGame));
    }
    @Test
    void joinGame() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, "JuanitaPablo", null, "testing", new ChessGame());
        dataAccess.createGame(testGame);
        assertDoesNotThrow(()-> dataAccess.updateGame(testGame));
    }

    @Test
    void joinGameBadId()  {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(1023655440, "JuanitaPablo", null, "testing", new ChessGame());
        assertThrows(ResponseException.class, ()-> dataAccess.updateGame(testGame));
    }

    @Test
    void getGame() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, "JuanitaPablo", null, "testing", new ChessGame());
        dataAccess.createGame(testGame);
        assertEquals(dataAccess.getGame(testGame.gameID()).game(), testGame.game());
    }

    @Test
    void getBadGame() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, "JuanitaPablo", null, "testing", new ChessGame());
        dataAccess.createGame(testGame);
        assertNull(dataAccess.getGame(23445));
    }










}
