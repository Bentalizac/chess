package dataAccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess () throws ResponseException {
        configureDataBase();
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(50) NOT NULL,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
              `userData` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              UNIQUE KEY `username_UNIQUE` (`username`),
              UNIQUE KEY `email_UNIQUE` (`email`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            
            """,

            """
            CREATE TABLE IF NOT EXISTS authData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(50) NOT NULL,
              `authToken` varchar(100) NOT NULL,
              `authData` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              UNIQUE KEY `username_UNIQUE` (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,

            """
            CREATE TABLE IF NOT EXISTS gameData (
              `id` int NOT NULL,
              `gameName` varchar(50) NOT NULL,
              `whiteUsername` varchar(100) NOT NULL,
              `blackUsername` varchar(100) NOT NULL,
              `gameJSON` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            
            """
    };

    private int executeUpdate(String statement, Object... params) throws ResponseException {

        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param instanceof AuthData p) ps.setString(i + 1, p.toString());
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
        catch(SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDataBase() throws ResponseException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


    private String encryptPassword(String rawPassword) { // TODO actually encrypt the password
        return rawPassword;
    }

    public void clearData() throws ResponseException {
        String statement = "TRUNCATE TABLE userData, authData, gameData";
        executeUpdate(statement);
    }

    @Override
    public UserData getUser(String userName) throws ResponseException {
        return null;
    }

    @Override
    public ArrayList<UserData> getAllUsers() {
        return null;
    }

    @Override
    public AuthData login(String username, String authToken) {
        return null;
    }

    @Override
    public AuthData getUserByAuth(String authtoken) throws ResponseException {
        return null;
    }
    @Override
    public void createUser(UserData user) throws ResponseException{
        String statement = "INSERT INTO userData (username, password, email, userData) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(user);
        var id = executeUpdate(statement, user.username(), encryptPassword(user.password()), user.email(), json);
    }

    @Override
    public void logout(AuthData data) {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

    @Override
    public int getNextID() {
        return 0;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }
}
