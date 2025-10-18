package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

public class UserService {

    private final DataAccess dataAccess;

    public UserService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException, DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new ResponseException("Error: Empty field");
        }
        UserData user = new UserData(req.username(), req.password(), req.email());
        return dataAccess.createUser(user);
    }

//    public LoginResult login(LoginRequest req) {}

}
