package dataaccess;

import model.AuthData;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;

import javax.xml.crypto.Data;

public interface UserDataAccess {

    RegisterResult createUser(UserData user) throws DataAccessException;

    AuthData getAuthData(UserData user) throws DataAccessException;

    LoginResult loginUser(String username, String password) throws DataAccessException;

    String logoutUser(String authToken) throws DataAccessException;

    void clear();
}
