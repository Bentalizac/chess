package dataAccessTests;

import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class SQLUserServiceTests {
    static MySQLDataAccess dataAccess = new MySQLDataAccess();
    static final UserService service = new UserService(dataAccess);

    @BeforeEach
    void clear() throws ResponseException {
        service.deleteAllData();
    }

    @Test
    void addUser() throws ResponseException {
        UserData user = new UserData("JuanPablo", "password", "yee@haw.com");
        AuthData returnValue = service.createUser(user);
        var allUsers = service.getUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.contains(user));
    }

    @Test
    void userAlreadyExists() throws ResponseException { // Negative addUser() test
        UserData user = new UserData("JuanPablo", "password", "yee@haw.com");
        AuthData returnValue = service.createUser(user);
        assertThrows(ResponseException.class, () -> {service.createUser(user);}, "USER ALREADY EXISTS");
    }

    @Test
    void login() throws ResponseException {
        UserData user = new UserData("JuanPablo", "password", "yee@haw.com");
        AuthData userAuth = service.createUser(user);
        service.logout(userAuth.authToken());
        assertDoesNotThrow(()-> service.login(user.username(), user.password()));
    }

    @Test
    void loginBadUser() throws ResponseException {
        assertThrows(ResponseException.class, ()-> {service.login("JaneNotHere", "correctPassword");}, "USER NOT FOUND");
    }

    @Test
    void loginBadPassword() throws ResponseException {
        UserData data = new UserData("JaneHere", "correctPassword", "email@email.email");
        AuthData userAuth = service.createUser(data);
        service.logout(userAuth.authToken()); // Logout safe to use here because it has been shown to work
        assertThrows(ResponseException.class, ()-> {service.login("JaneHere", "incorrectPassword");}, "USER NOT FOUND");
    }

    @Test
    void logout() throws ResponseException {
        UserData user = new UserData("JuanPablo", "password", "yee@haw.com");
        AuthData returnValue = service.createUser(user);
         // in this case we have access to both the username and the authtoken, but that might not always be true
        assertDoesNotThrow(()->{service.logout(returnValue.authToken());});
    }

    @Test
    void logoutNonexistantUser() throws ResponseException {
        UserData user = new UserData("JohnCena", "youcantseeme", "playsthemeonkazoo@weewoo.org");
        String bad_token = UUID.randomUUID().toString(); // The odds that this works as a valid token are so astromomically low it is a non issue
        assertThrows(ResponseException.class, ()-> {service.logout(bad_token);}, "USER NOT FOUND");
    }

    @Test
    void logoutTwice() throws ResponseException {
        UserData user = new UserData("JohnCena", "youcantseeme", "playsthemeonkazoo@weewoo.org");
        AuthData returnValue = service.createUser(user);
        service.logout(returnValue.authToken());
        assertThrows(ResponseException.class, ()-> {service.logout(returnValue.authToken());}, "USER NOT LOGGED IN");
    }


}
