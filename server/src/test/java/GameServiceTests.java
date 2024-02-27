import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTests {

    static MemoryDataAccess dataAccess = new MemoryDataAccess();
    static final UserService userService = new UserService(dataAccess);
    static  GameService service = new GameService(dataAccess);

    private String testAuthToken = "";

    private static final UserData existingUser = new UserData("existingUser", "correctPassword", "valid@email.com");

    void positiveSetup() throws ResponseException {
        //UserData existingUser = new UserData("existingUser", "correctPassword", "valid@email.com");
        AuthData returnValue = userService.createUser(existingUser);
        var allUsers = userService.getUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.contains(existingUser));
        this.testAuthToken = returnValue.authToken();
        //return returnValue;
    }

    @BeforeEach
    void clear() throws ResponseException {
        userService.deleteAllData();
        this.positiveSetup();
    }



    @Test
    void createGame() throws ResponseException {
        String testname = "testgame";
        assertDoesNotThrow( ()-> {service.createGame(testname, this.testAuthToken);} );
    }

    @Test
    void createGameBadAuth() throws ResponseException {
        String testname = "testgame";
        assertThrows(ResponseException.class, ()-> {service.createGame(testname, "BADAUTH");}, "error: TOKEN NOT AUTHORIZED");
    }

    @Test
    void watchGame() throws ResponseException {
        String testname = "testgame";
        int gameID = service.createGame(testname, this.testAuthToken);
        JoinGameRequest testJoin = new JoinGameRequest(null, gameID);
        assertDoesNotThrow(()-> {service.joinGame(testJoin, this.testAuthToken);});
    }
    @Test
    void whiteJoinGame() throws ResponseException {
        String testname = "testgame";
        int gameID = service.createGame(testname, this.testAuthToken);
        JoinGameRequest testJoin = new JoinGameRequest("WHITE", gameID);
        assertDoesNotThrow(()-> {service.joinGame(testJoin, this.testAuthToken);});
    }

    @Test
    void joinOccupiedPlayer()throws ResponseException {
        String testname = "testgame";
        int gameID = service.createGame(testname, this.testAuthToken);
        JoinGameRequest testJoin = new JoinGameRequest("WHITE", gameID);
        assertDoesNotThrow(()-> {service.joinGame(testJoin, this.testAuthToken);});

        assertThrows(ResponseException.class, ()-> {service.joinGame(testJoin, this.testAuthToken);}, "error: USER ALREADY TAKEN");
    }
    @Test
    void joinNonexistantGame() throws ResponseException {
        int gameID = 334223;
        JoinGameRequest testJoin = new JoinGameRequest(null, gameID);
        assertThrows(ResponseException.class, ()-> {service.joinGame(testJoin, this.testAuthToken);}, "error: GAME DOES NOT EXIST");
    }



}
