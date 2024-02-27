import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;


public class GameServiceTests {

    static final UserService service = new UserService();

    @BeforeEach
    void clear() throws ResponseException {
        service.deleteAllData();
    }



}
