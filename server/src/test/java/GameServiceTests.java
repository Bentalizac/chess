import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
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

    AuthData positiveSetup() throws ResponseException {
        //UserData existingUser = new UserData("existingUser", "correctPassword", "valid@email.com");
        AuthData returnValue = userService.createUser(existingUser);
        var allUsers = userService.getUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.contains(existingUser));
        return returnValue;
    }

    @BeforeEach
    void clear() throws ResponseException {
        userService.deleteAllData();
    }



    @Test
    void createGame() throws ResponseException {
        String testname = "testgame";
        AuthData returnValue = userService.createUser(existingUser);
        var allUsers = userService.getUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.contains(existingUser));

        assertDoesNotThrow( ()-> {service.createGame(testname, returnValue.authToken());} );
    }



}
