package dataaccess;

import model.CreateGameResult;
import model.GameData;
import model.JoinGameRequest;
import service.AlreadyTakenException;
import service.EmptyFieldException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDataAccess implements GameDataAccess{

    MySqlGameDataAccess(){
        configureDataBase();
    }

    void clear(){

    }

    ArrayList<GameData> listGames(){

    }

    CreateGameResult createGame(String gameName){

    }

    void joinGame(JoinGameRequest req, String username){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              gameID int NOT NULL,
              whiteUsername varchar(256),
              blackUsername varchar(256),
              gameName varchar(256) NOT NULL,
              json TEXT DEFAULT NULL,
              INDEX(gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};


    public void configureDataBase() throws DataAccessException {
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

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
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
}
