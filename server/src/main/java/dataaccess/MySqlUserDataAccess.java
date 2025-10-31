package dataaccess;

import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.EmptyFieldException;
import service.UnauthorizedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlUserDataAccess implements UserDataAccess {

    public MySqlUserDataAccess(){
        configureDatabase();
    }

    public RegisterResult createUser(UserData user) throws UnauthorizedException {
        if (getAuthToken(user.username()) != null){
            throw new AlreadyTakenException("Error: username already taken");
        }
        var userDataStatement = "INSERT INTO UserData (Username, Password, Email) VALUES (?, ?, ?)";
        var securedPassword = generateHashedPassword(user.password());
        executeUpdate(userDataStatement, user.username(), securedPassword, user.email());

        String authToken = createAuthToken();
        var authDataStatement = "INSERT INTO AuthData (authToken, Username) VALUES (?, ?)";
        executeUpdate(authDataStatement, authToken, user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public boolean verifyAuthData(String authToken){
        var statement = "SELECT authToken FROM AuthData WHERE authToken=?";
        return null != getItemInDatabase(statement, authToken, "authToken");
    }

    public LoginResult loginUser(String username, String password) throws UnauthorizedException{
        var statement = "SELECT Password FROM UserData WHERE Username=?";
        var verifiedUserPassword = getItemInDatabase(statement, username, "Password");
        if (verifiedUserPassword == null){
            throw new UnauthorizedException("Error: Username does not exist");
        }
        if (!BCrypt.checkpw(password, verifiedUserPassword)){
            throw new UnauthorizedException("Error: Incorrect password");
        }
        String authToken = createAuthToken();
        var authDataStatement = "INSERT INTO AuthData (authToken, Username) VALUES (?, ?)";
        executeUpdate(authDataStatement, authToken, username);
        return new LoginResult(username,authToken);
    }

    public String deleteAuthToken(String authToken) throws UnauthorizedException{
        if (!verifyAuthData(authToken)){throw new UnauthorizedException("Error: unauthorized action");}
        var statement = "DELETE FROM AuthData WHERE authToken=?";
        executeUpdate(statement, authToken);
        return "{}";
    }

    public String getUsername(String authToken){
        var statement = "SELECT authToken, Username FROM AuthData WHERE authToken=?";
        return getItemInDatabase(statement, authToken, "Username");
    }

    public String getAuthToken(String username){
        var statement = "SELECT authToken, Username FROM AuthData WHERE Username=?";
        return getItemInDatabase(statement, username, "authToken");
    }

    public void clear(){
        try {
            var statement1 = "TRUNCATE UserData";
            var statement2 = "TRUNCATE AuthData";
            executeUpdate(statement1);
            executeUpdate(statement2);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Error: unable to execute query", e);
        }

    }

    private String createAuthToken(){
        return UUID.randomUUID().toString();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  UserData (
              Username varchar(256) NOT NULL,
              Password varchar(256) NOT NULL,
              Email varchar(256) NOT NULL,
              PRIMARY KEY (Username),
              INDEX(Username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            """
            CREATE TABLE IF NOT EXISTS AuthData (
              authToken varchar(256) NOT NULL,
              Username varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    ps.setString(i + 1, (String)param);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()){
                default:
                    throw new DataAccessException("Error: Unable to execute query", e);
            }
        }
    }


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString(), ex);
        }
    }

    private String generateHashedPassword(String clearTextPassword){
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private String getItemInDatabase(String statement, String item, String type){
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, item);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(String.format("%s", type));
                    }
                }
                catch (Exception e){
                    return null;
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: failed to connect to db", e);
        }
        return null;
    }
}
