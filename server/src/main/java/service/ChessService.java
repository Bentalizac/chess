package service;
import dataAccess.DataAccess;

import exception.ResponseException;
import model.UserData;
import model.AuthData;
import model.GameData;
import spark.Response;

public interface ChessService {
    void deleteAllData() throws ResponseException;

}
