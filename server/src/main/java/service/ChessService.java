package service;
import dataAccess.DataAccess;

import exception.ResponseException;
import model.UserData;
import model.AuthData;
import model.GameData;
import spark.Response;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteAllData() throws ResponseException {
        dataAccess.clearData();
    }

}
