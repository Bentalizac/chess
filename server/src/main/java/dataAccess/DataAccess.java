package dataAccess;

import exception.ResponseException;
import model.UserData;


public interface DataAccess {

    void clearData() throws ResponseException;

    UserData getUser(String userName) throws ResponseException;



}
