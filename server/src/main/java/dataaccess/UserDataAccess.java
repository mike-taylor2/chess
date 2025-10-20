package dataaccess;

import model.LoginResult;
import model.RegisterResult;
import model.UserData;

public interface UserDataAccess {

    RegisterResult createUser(UserData user) throws DataAccessException;

    boolean verifyAuthData(String authToken);

    LoginResult loginUser(String username, String password) throws DataAccessException;

    String deleteAuthToken(String authToken) throws DataAccessException;

    void clear();
}
