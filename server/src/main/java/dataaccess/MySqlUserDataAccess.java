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
        var userDataStatement = "INSERT INTO UserData (Username, Password, Email) VALUES (?, ?, ?)";
        var securedPassword = generateHashedPassword(user.password());
        executeUpdate(userDataStatement, user.username(), securedPassword, user.email());

        String authToken = createAuthToken();
        var authDataStatement = "INSERT INTO AuthData (authToken, Username) VALUES (?, ?)";
        executeUpdate(authDataStatement, authToken, user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public boolean verifyAuthData(String authToken){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT Username FROM AuthData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    LoginResult loginUser(String username, String password) throws UnauthorizedException{

    }

    String deleteAuthToken(String authToken) throws UnauthorizedException{

    }

    String getUsername(String authToken){

    }

    String getAuthToken(String username){

    }

    void clear(){
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
            CREATE TABLE IF NOT EXISTS AuthData (
              authToken varchar(256) NOT NULL,
              Username varchar(256) NOT NULL,
              PRIMARY KEY (Username),
              INDEX(Username),
              FOREIGN KEY (Username) REFERENCES UserData(Username)
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
                case 1062:
                    throw new AlreadyTakenException("Error: Username is already taken");
                case 1048:
                    throw new EmptyFieldException("Error: One of the fields is empty");
                default:
                    throw new DataAccessException("Error: Unable to execute query", e);
            }
        }
    }


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure database", ex);
        }
    }

    private String generateHashedPassword(String clearTextPassword){
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
