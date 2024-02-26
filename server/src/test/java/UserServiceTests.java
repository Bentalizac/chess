import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
public class UserServiceTests {

    static final UserService service = new UserService();

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
        AuthData returnValue = service.createUser(user);


    }


}
