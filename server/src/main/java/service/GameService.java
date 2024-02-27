package service;

import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;


public class GameService {

    // This class touches nothing in the ChessGame game field of the gameData record class. That may need to be changed later
    private final MemoryDataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private AuthData isAuthorized(String authToken) {
        return dataAccess.getUserByAuth(authToken);
    }

    private GameData buildNewGame(String gameName) { // Builds blank GameData records
        int gameID = dataAccess.getNextID();
        return new GameData(gameID, null, null, gameName, null);
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
                if (existingGame.blackUsername() != null){
                    throw new ResponseException(403, "error: USER ALREADY TAKEN");
                }
                newGameData = new GameData(existingGame.gameID(), existingGame.whiteUsername(), authorization.username(), existingGame.gameName(), existingGame.game());
            }
            else if(info.playerColor().equals("WHITE")) {
                if (existingGame.whiteUsername() != null){
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
