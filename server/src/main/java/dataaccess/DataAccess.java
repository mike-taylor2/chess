package dataaccess;

import model.AuthData;
import model.RegisterResult;
import model.UserData;

public interface DataAccess {

    RegisterResult createUser(UserData user) throws DataAccessException;

    AuthData getAuthToken(UserData user);
}
