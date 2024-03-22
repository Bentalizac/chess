package clientTests;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static UserData validUser;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(port);

        validUser = new UserData("a", "a", "a");

    }

    @AfterAll
    static void stopServer() {
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
        if(response.getClass() == UserData.class) {
            UserData goodResponse = new UserData(((UserData) response).username(), ((UserData) response).password(), ((UserData) response).email());
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

}