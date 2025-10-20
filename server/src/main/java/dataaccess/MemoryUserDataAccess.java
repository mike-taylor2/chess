package dataaccess;

import model.AuthData;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;



public class MemoryUserDataAccess implements UserDataAccess {
    ArrayList<UserData> userList = new ArrayList<>();
    ArrayList<AuthData> usernameTokenList = new ArrayList<>();

    public RegisterResult createUser(UserData user) throws DataAccessException{
        if (duplicatedUsername(user)){
            throw new DataAccessException("Error: username already exists");
        }
        String authToken = createAuthToken();
        userList.add(user);
        usernameTokenList.add(new AuthData(authToken, user.username()));
        return new RegisterResult(user.username(), authToken);
    }

    public LoginResult loginUser(String username, String password) throws DataAccessException {
        if (unmatchedUsernamePassword(username, password)){
            throw new DataAccessException("Error: unauthorized");
        }
        if (userAlreadyLoggedIn(username)){
            throw new DataAccessException("Error: user already logged in");
        }
        String authToken = createAuthToken();
        usernameTokenList.add(new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public String deleteAuthToken(String authToken) throws DataAccessException {
        for (AuthData a : usernameTokenList){
            if (a.authToken().equals(authToken)){
                usernameTokenList.remove(a);
                return "{}";
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    public boolean verifyAuthData(String authToken){
        for (AuthData a : usernameTokenList){
            if (a.authToken().equals(authToken)){
                return true;
            }
        }
        return false;
    }

    public void clear(){
        userList.clear();
        usernameTokenList.clear();
    }

    private String createAuthToken(){
        return UUID.randomUUID().toString();
    }

    private boolean duplicatedUsername(UserData user){
        for (UserData u : userList){
            if (u.equals(user)) {return true;}
        }
        return false;
    }

    private boolean unmatchedUsernamePassword(String username, String password){
        for (UserData u : userList){
            if (u.username().equals(username) && u.password().equals(password)) {return false;}
        }
        return true;
    }

    private boolean userAlreadyLoggedIn(String username){
        for (AuthData a : usernameTokenList){
            if (a.username().equals(username)){return true;}
        }
        return false;
    }
}
