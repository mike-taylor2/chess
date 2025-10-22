package dataaccess;

import model.AuthData;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.util.ArrayList;
import java.util.UUID;



public class MemoryUserDataAccess implements UserDataAccess {
    ArrayList<UserData> userList = new ArrayList<>();
    ArrayList<AuthData> usernameTokenList = new ArrayList<>();

    public RegisterResult createUser(UserData user) throws UnauthorizedException {
        if (duplicatedUsername(user)){
            throw new AlreadyTakenException("Error: Username already exists");
        }
        String authToken = createAuthToken();
        userList.add(user);
        usernameTokenList.add(new AuthData(authToken, user.username()));
        return new RegisterResult(user.username(), authToken);
    }

    public LoginResult loginUser(String username, String password) throws UnauthorizedException {
        if (unmatchedUsernamePassword(username, password)){
            throw new UnauthorizedException("Error: Username or password is incorrect");
        }

        String authToken = createAuthToken();
        usernameTokenList.add(new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public String deleteAuthToken(String authToken) throws UnauthorizedException {
        for (AuthData a : usernameTokenList){
            if (a.authToken().equals(authToken)){
                usernameTokenList.remove(a);
                return "{}";
            }
        }
        throw new UnauthorizedException("Error: Not authorized");
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

    public String getUsername(String authToken){
        for (AuthData a : usernameTokenList){
            if (a.authToken().equals(authToken)) return a.username();
        }
        return null;
    }

    public String getAuthToken(String username){
        for (AuthData a : usernameTokenList){
            if (a.username().equals(username)) return a.authToken();
        }
        return null;
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
}
