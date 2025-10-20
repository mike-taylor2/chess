package service;

import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataAccess;
import model.*;

public class UserService {

    private final UserDataAccess dataAccess;

    public UserService() {
        this.dataAccess = new MemoryUserDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException, DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new ResponseException("Error: Empty field");
        }
        UserData user = new UserData(req.username(), req.password(), req.email());
        return dataAccess.createUser(user);
    }

    public LoginResult login(LoginRequest req) throws ResponseException, DataAccessException{
        if (req.username() == null || req.password() == null){
            throw new ResponseException("Error: Empty field");
        }
        return dataAccess.loginUser(req.username(), req.password());
    }

    public UserDataAccess getDataAccess(){
        return dataAccess;
    }

}
