package dataAccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess ()  {
        try {
            configureDataBase();
        }
        catch(Exception e){
            //ignore
        }
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
              UNIQUE KEY `username_UNIQUE` (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            
            """,

            """
            CREATE TABLE IF NOT EXISTS authData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(50) NOT NULL,
              `authToken` varchar(100) NOT NULL,
              `authData` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,

            """
            CREATE TABLE IF NOT EXISTS gameData (
              `id` int NOT NULL,
              `gameName` varchar(50) NOT NULL,
              `whiteUsername` varchar(100) DEFAULT NULL,
              `blackUsername` varchar(100) DEFAULT NULL,
              `gameJSON` TEXT DEFAULT NULL,
              `gameData` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            
            """
    };

    private int executeUpdate(String statement, Object... params) throws ResponseException { // Can be used for C, U, and D, not R
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case UserData p -> ps.setString(i + 1, p.toString());
                        case AuthData p -> ps.setString(i + 1, p.toString());
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
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

    private ArrayList<UserData> getUserRecord(String statement, Object... params) {
        ArrayList<UserData> result = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)) {

                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                }
                try (var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(resultToUser(rs));
                    }
                }
            }
        }
        catch(SQLException e) {
            return null;
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private ArrayList<AuthData> getAuthRecord(String statement, Object... params) {
        ArrayList<AuthData> result = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)) {

                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                }
                try (var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(resultToAuth(rs));
                    }
                }
            }
        }
        catch(SQLException e) {
            return null;
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private ArrayList<GameData> getGameRecord(String statement, Object... params) {
        ArrayList<GameData> result = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)) {

                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                }
                try (var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(resultToGame(rs));
                    }
                }
            }
        }
        catch(SQLException e) {
            return null;
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return result;
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



    public void clearData() {
        try {
            String statement = "TRUNCATE TABLE userData";
            executeUpdate(statement);
            statement = "TRUNCATE TABLE authData";
            executeUpdate(statement);
            statement = "TRUNCATE TABLE gameData";
            executeUpdate(statement);
        }
        catch(Exception e){
            //ignore
        }
    }

    private UserData resultToUser(ResultSet response){
        if(response == null) {
            return null;
        }
        try {
            var json = response.getString("userData");
            return new Gson().fromJson(json, UserData.class);
        }
        catch(SQLException ex) {
            return null;
        }
    }

    private AuthData resultToAuth(ResultSet response){
        if(response == null) {
            return null;
        }
        try {
            var json = response.getString("authData");
            return new Gson().fromJson(json, AuthData.class);
        }
        catch(SQLException ex) {
            return null;
        }
    }

    private GameData resultToGame(ResultSet response){
        if(response == null) {
            return null;
        }
        try {
            var json = response.getString("gameData");
            return new Gson().fromJson(json, GameData.class);
        }
        catch(SQLException ex) {
            return null;
        }
    }

    public UserData getUser(String userName) {
        String statement = "SELECT userData FROM userData WHERE username= ? ";
        ArrayList<UserData> response = getUserRecord(statement, userName);
        assert response != null;
        if(!response.isEmpty()){
            return response.getFirst();
        }
        else{
            return null;
        }
    }

    @Override
    public ArrayList<UserData> getAllUsers () {
        String statement = "SELECT userData FROM userData";
        return getUserRecord(statement);
    }


    @Override
    public AuthData login(String username, String authToken) throws ResponseException{
        AuthData auth = new AuthData(authToken, username);
        String statement = "INSERT INTO authData (username, authToken, authData) VALUES (?,?,?)";
        var json = new Gson().toJson(auth);
        executeUpdate(statement, auth.username(), auth.authToken(), json);
        return auth;
    }

    @Override
    public AuthData getUserByAuth(String authtoken)  {

        String statement = "SELECT authData FROM authData WHERE authToken=?";
        var authDatas = getAuthRecord(statement, authtoken);

        if(authDatas == null) {
            return null;
        } else if (authDatas.isEmpty()) {
            return null;
        }
        else{
            return authDatas.getFirst();
        }
    }

    @Override
    public void createUser(UserData user) throws ResponseException{
        String statement = "INSERT INTO userData (username, password, email, userData) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(user);
        executeUpdate(statement, user.username(), user.password(), user.email(), json);
    }

    @Override
    public void logout(AuthData data) {
        String statement = "DELETE FROM authData WHERE authToken=?";
        try {
            executeUpdate(statement, data.authToken());
        }
        catch(ResponseException e) {
            //ignore
        }
    }

    @Override
    public ArrayList<GameData> getGames() {
        String statement = "SELECT * FROM gameData";

        return getGameRecord(statement);
    }
    private int nextGameId = 1;
    @Override
    public int getNextID() {
        return nextGameId;
    }

    @Override
    public void createGame(GameData game) throws ResponseException{
        var json = new Gson().toJson(game);
        var gameJson = new Gson().toJson(game.game());
        String statement ="INSERT INTO gameData (id, gameName, whiteUsername, blackUsername, gameJSON, gameData) VALUES (?,?,?,?,?,?)";
        executeUpdate(statement, game.gameID(), game.gameName(),game.whiteUsername(),game.blackUsername(), gameJson, json);
        this.nextGameId += 1;
    }


    @Override
    public GameData getGame(int gameID) {
        String statement = "SELECT gameData FROM gameData WHERE id=?";
        var gameDataResponse = getGameRecord(statement, gameID);

        if(gameDataResponse == null) {
            return null;
        } else if (gameDataResponse.isEmpty()) {
            return null;
        }
        else{
            return gameDataResponse.getFirst();
        }
    }

    private boolean gameExists(GameData game) throws ResponseException {
        //String statement = "SELECT * FROM gameData WHERE id =?";
        var response = getGame(game.gameID());
        return response != null;

    }
    @Override
    public void updateGame(GameData game) throws ResponseException{
        var json = new Gson().toJson(game);

        if(!gameExists(game)) {
            throw new ResponseException(400, "error: GAME DOES NOT EXIST");
        }

        String statement = "UPDATE gameData SET whiteUsername = ? , blackUsername = ?, gameData = ? WHERE id =?";
        var id = executeUpdate(statement, game.whiteUsername(),game.blackUsername(), json, game.gameID());
        System.out.println(id);
    }
}
