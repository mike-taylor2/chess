package dataaccess;

import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import service.UnauthorizedException;

public interface UserDataAccess {

    RegisterResult createUser(UserData user) throws UnauthorizedException;

    boolean verifyAuthData(String authToken);

    LoginResult loginUser(String username, String password) throws UnauthorizedException;

    String deleteAuthToken(String authToken) throws UnauthorizedException;

    void clear();
}
