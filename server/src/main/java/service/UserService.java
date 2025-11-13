package service;

import dataaccess.MySqlUserDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.MemoryUserDataAccess;
import model.*;

public class UserService {

    private final UserDataAccess userData;

    public UserService() {
        this.userData = new MemoryUserDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws EmptyFieldException {
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new EmptyFieldException("Error: One or more fields are empty");
        }
        UserData user = new UserData(req.username(), req.password(), req.email());
        return userData.createUser(user);
    }

    public LoginResult login(LoginRequest req) throws EmptyFieldException {
        if (req.username() == null || req.password() == null){
            throw new EmptyFieldException("Error: One or more fields are empty");
        }
        return userData.loginUser(req.username(), req.password());
    }

    public String logout(LogoutRequest req) throws UnauthorizedException{
        return userData.deleteAuthToken(req.authToken());
    }

    public void verifyAuthData(String authToken) throws UnauthorizedException{
        if (!userData.verifyAuthData(authToken)){
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public String getUsername(String authToken) {
        return userData.getUsername(authToken);
    }

    public UserDataAccess getUserData(){
        return userData;
    }

}
