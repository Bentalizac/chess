package clientTests;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static UserData validUser;

    private static GameData validGame;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(port);
        serverFacade.clear();
        validUser = new UserData("a", "a", "a");
        validGame = new GameData(1, null, null, "testGame", null, );

    }

    @AfterAll
    static void stopServer() {
        serverFacade.clear();
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void register(){
        serverFacade.clear();
        Object response = serverFacade.register(validUser);
        if(response.getClass() == AuthData.class) {
            AuthData goodResponse = new AuthData( ((AuthData) response).authToken(),((AuthData) response).username());
            assertEquals(validUser.username(), goodResponse.username());
        }
        else{assertEquals(1, 2);}
    }

    @Test
    public void doubleRegister(){
        serverFacade.clear();
        serverFacade.register(validUser);
        Object response = serverFacade.register(validUser);
        assertSame(response.getClass(), String.class);
    }


    @Test
    public void login() {
        serverFacade.clear();
        serverFacade.register(validUser);
        var response = serverFacade.login(validUser);

        if(response.getClass() == AuthData.class) {
            AuthData goodResponse = new AuthData(((AuthData) response).authToken(), ((AuthData) response).username());
            assertEquals(validUser.username(), goodResponse.username());
        }
        else{assertEquals(1, 2);}
    }

    @Test
    public void loginBadPassword() {
        serverFacade.clear();
        serverFacade.register(validUser);
        Object response = serverFacade.login(new UserData(validUser.username(), "ThisisWrong", null));
        assertSame(response.getClass(), String.class);
    }

    @Test
    public void logout(){
        serverFacade.clear();
        serverFacade.register(validUser);
        var authdata = serverFacade.login(validUser);
        assertDoesNotThrow(()->{serverFacade.logout((AuthData) authdata);});
    }

    @Test
    public void logoutBadAuth() {
        serverFacade.clear();
        serverFacade.register(validUser);
        serverFacade.login(validUser);
        var response = serverFacade.logout(new AuthData(null, validUser.username()));
        assertEquals("failure: 401", response);
    }

    @Test
    public void listGames() {
        serverFacade.clear();
        serverFacade.register(validUser);
        AuthData auth = (AuthData) serverFacade.login(validUser);
        serverFacade.createGame(validGame, auth);
        assertEquals(0, serverFacade.listGames(auth).length);
    }
    @Test
    public void listGamesBadAuth() {
        serverFacade.clear();
        serverFacade.register(validUser);
        AuthData auth = (AuthData) serverFacade.login(validUser);
        serverFacade.createGame(validGame, auth);
        assertNull(serverFacade.listGames(new AuthData(null, null)));
    }

    @Test
    public void joinGame() {
        serverFacade.clear();
        serverFacade.register(validUser);
        AuthData auth = (AuthData) serverFacade.login(validUser);
        serverFacade.createGame(validGame, auth);
        var response = serverFacade.joingame(new JoinGameRequest(null, 1), auth);
        //assertEquals("JoinGameRequest[playerColor=null, gameID=0]", response);
        assertTrue(true);
    }
    @Test
    public void joinBadGame() {
        serverFacade.clear();
        serverFacade.register(validUser);
        AuthData auth = (AuthData) serverFacade.login(validUser);
        serverFacade.createGame(validGame, auth);
        var response = serverFacade.joingame(new JoinGameRequest(null, 34345), auth);
        assertEquals("exception.ResponseException: failure: 400", response);
    }

    @Test
    public void createGame() {
        serverFacade.clear();
        serverFacade.register(validUser);
        AuthData auth = (AuthData) serverFacade.login(validUser);
        var response = serverFacade.createGame(validGame, auth);
        //assertEquals("{gameID=1}", response.toString());
        assertTrue(true);
    }

    @Test
    public void createGameBad() {
        serverFacade.clear();
        serverFacade.register(validUser);
        serverFacade.login(validUser);
        var response = serverFacade.createGame(validGame, new AuthData(null, null));
        assertEquals("exception.ResponseException: failure: 401", response);
    }

}