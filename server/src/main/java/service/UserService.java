package service;

import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataAccess;
import model.*;

public class UserService {

    private final UserDataAccess userData;

    public UserService() {
        this.userData = new MemoryUserDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException, DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new ResponseException("Error: Empty field");
        }
        UserData user = new UserData(req.username(), req.password(), req.email());
        return userData.createUser(user);
    }

    public LoginResult login(LoginRequest req) throws ResponseException, DataAccessException{
        if (req.username() == null || req.password() == null){
            throw new ResponseException("Error: Empty field");
        }
        return userData.loginUser(req.username(), req.password());
    }

    public String logout(LogoutRequest req) throws DataAccessException{
        return userData.deleteAuthToken(req.authToken());
    }

    public boolean verifyAuthData(String authToken){
        return userData.verifyAuthData(authToken);
    }

    public UserDataAccess getUserData(){
        return userData;
    }

}
