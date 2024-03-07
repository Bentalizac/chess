import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class SQLDataAccessTests {

    private DataAccess getDataAccess() throws ResponseException {
        return new MySQLDataAccess();
    }

    private void setup() throws ResponseException{
        DataAccess dataAccess = getDataAccess();
        dataAccess.clearData();

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

    @Test
    void login() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        assertDoesNotThrow(()->dataAccess.login(testUser.username(), testUser.password()));

    }

    @Test
    void loginBadUser() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitoPabloy", "password", "yee@haw.com");
        assertThrows(ResponseException.class, ()->dataAccess.login(testUser.username(), testUser.password()));
    }

    // There isn't a loginBadPassword because the DAO doesn't have the password checking logic

    @Test
    void logout() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testUser = new UserData("JuanitaPablo", "password", "yee@haw.com");
        AuthData auth = dataAccess.login(testUser.username(),testUser.password());

        assertDoesNotThrow(()->dataAccess.logout(auth));
    }


    @Test
    void createGame() throws  ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, null, null, "testing", new ChessGame());
        assertDoesNotThrow(()-> dataAccess.createGame(testGame));
    }

    @Test
    void watchGame() throws  ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, null, null, "testing", new ChessGame());
        assertDoesNotThrow(()-> dataAccess.updateGame(testGame));
    }

    @Test
    void joinGame() throws  ResponseException {
        DataAccess dataAccess = getDataAccess();
        createGame();
        var testGame = new GameData(100, "JuanitaPablo", null, "testing", new ChessGame());
        assertDoesNotThrow(()-> dataAccess.updateGame(testGame));

        GameData result = dataAccess.getGame(testGame.gameID());

        System.out.println("Space to debug");
    }

    @Test
    void getGame() throws ResponseException {
        DataAccess dataAccess = getDataAccess();
        var testGame = new GameData(100, "JuanitaPablo", null, "testing", new ChessGame());
        assertEquals(dataAccess.getGame(testGame.gameID()).game(), testGame.game());
    }










}
