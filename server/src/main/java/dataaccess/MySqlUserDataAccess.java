package dataaccess;

import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import service.UnauthorizedException;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDataAccess implements UserDataAccess {
    RegisterResult createUser(UserData user) throws UnauthorizedException {

    }

    boolean verifyAuthData(String authToken){

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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database", ex);
        }
    }
}
