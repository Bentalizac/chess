package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;


public class GameService {

    // This class touches nothing in the ChessGame game field of the gameData record class. That may need to be changed later
    private final DataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameService(MySQLDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    private AuthData isAuthorized(String authToken) {
        try {
            return dataAccess.getUserByAuth(authToken);
        } catch (ResponseException e) {
            return null;
        }
    }

    private GameData buildNewGame(String gameName) { // Builds blank GameData records
        int gameID = dataAccess.getNextID();
        return new GameData(gameID, null, null, gameName, new ChessGame());
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        AuthData authorization = isAuthorized(authToken);
        if (authorization == null || authorization.authToken() == null) {
            throw new ResponseException(401, "error: TOKEN NOT AUTHORIZED");
        }
        GameData game = this.buildNewGame(gameName);
        dataAccess.createGame(game);
        return game.gameID();
    }

    private ArrayList<GameData> getGames() {
        return dataAccess.getGames();
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        AuthData authorization = isAuthorized(authToken);
        if (authorization == null || authorization.authToken() == null) {
            throw new ResponseException(401, "error: TOKEN NOT AUTHORIZED");
        }
        return this.getGames();
    }

    public GameData getGame(int gameID){
        try {
            return dataAccess.getGame(gameID);
        }
        catch (ResponseException ex) {
            return null;
        }
    }

    public void joinGame(JoinGameRequest info, String authToken) throws ResponseException{
        AuthData authorization = isAuthorized(authToken);
        if (authorization == null || authorization.authToken() == null) {
            throw new ResponseException(401, "error: TOKEN NOT AUTHORIZED");
        }
        GameData existingGame = dataAccess.getGame(info.gameID());

        if (existingGame == null) {
            throw new ResponseException(400, "error: GAME DOES NOT EXIST");
        }
        if (info.playerColor() != null) {
            GameData newGameData;
            if(info.playerColor().equals("BLACK")) {
                if (existingGame.blackUsername() != null && !existingGame.blackUsername().equals(authorization.username())){
                    throw new ResponseException(403, "error: USER ALREADY TAKEN");
                }
                newGameData = new GameData(existingGame.gameID(), existingGame.whiteUsername(), authorization.username(), existingGame.gameName(), existingGame.game());
            }
            else if(info.playerColor().equals("WHITE")) {
                if (existingGame.whiteUsername() != null  && !existingGame.whiteUsername().equals(authorization.username())){
                    throw new ResponseException(403, "error: USER ALREADY TAKEN");
                }
                newGameData = new GameData(existingGame.gameID(),  authorization.username(), existingGame.blackUsername(), existingGame.gameName(), existingGame.game());
            }
            else{
                throw new ResponseException(403, "error: BAD COLOR");
            }
            dataAccess.updateGame(newGameData);
        }
    }
}
