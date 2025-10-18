package dataaccess;

import model.AuthData;
import model.RegisterResult;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{
    ArrayList<UserData> userList = new ArrayList<>();
    HashMap<String, String> usernameTokenList = new HashMap<>();

    public RegisterResult createUser(UserData user) throws DataAccessException{
        if (duplicatedUsername(user)){
            throw new DataAccessException("Error: username already exists");
        }
        String authToken = createAuthToken();
        userList.add(user);
        usernameTokenList.put(user.username(), authToken);
        return new RegisterResult(user.username(), authToken);
    }

    public String createAuthToken(){
        return UUID.randomUUID().toString();
    }

    public AuthData getAuthToken(UserData user){
        return new AuthData(createAuthToken(), user.username());
    }

    private boolean duplicatedUsername(UserData user){
        for (UserData u : userList){
            if (u.equals(user)) return true;
        }
        return false;
    }
}
