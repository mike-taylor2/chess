package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.CreateGameResult;
import model.GameData;
import model.JoinGameRequest;
import service.AlreadyTakenException;
import service.EmptyFieldException;
import service.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDataAccess implements GameDataAccess{
    Integer gameID = 1001;

    public MySqlGameDataAccess(){
        configureDataBase();
    }

    public void clear(){
        var statement = "TRUNCATE GameData";
        executeUpdate(statement);
    }

    public ArrayList<GameData> listGames(){
        ArrayList<GameData> gameList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to execute query", e);
        }
        return gameList;
    }

    public CreateGameResult createGame(String gameName){
        int gameID = this.gameID;
        this.gameID += 1;
        String json = new Gson().toJson(new ChessGame());
        var statement = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, gameID, null, null, gameName, json);
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest req, String username){
        GameData game = findGame(req.gameID());
        if (req.playerColor() == null){
            throw new EmptyFieldException("Error: PlayerColor field is empty");
        }
        if (!(req.playerColor().equals("WHITE") || req.playerColor().equals("BLACK"))) {
            throw new EmptyFieldException("Error: PlayerColor field is empty");
        }

        if (req.playerColor().equals("WHITE") && game.whiteUsername() == null) {
            var joinedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            editGame(game, joinedGame);
        }
        else if (req.playerColor().equals("BLACK") && game.blackUsername() == null) {
            var joinedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            editGame(game, joinedGame);
        }
        else {
            throw new AlreadyTakenException("Error: Selected color was already taken");
        }
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
                    else if (param==null) ps.setString(i+1, null);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to execute query", e);
        }
    }

    private GameData readGame(ResultSet rs) throws DataAccessException{
        try{
            var gameID = rs.getInt("gameID");
            var whiteUsername = rs.getString("whiteUsername");
            var blackUsername = rs.getString("blackUsername");
            var gameName = rs.getString("gameName");
            var json = rs.getString("json");
            var ChessGame = new Gson().fromJson(json, ChessGame.class);
            return new GameData(gameID, whiteUsername, blackUsername, gameName, ChessGame);
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: Unable to execute query", e);
        }
    }

    private void editGame(GameData oldGame, GameData newGame) {
        var statement1 = "DELETE FROM gameData WHERE gameID=?";
        executeUpdate(statement1, oldGame.gameID());
        var statement2 = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        var json = new Gson().toJson(oldGame.game());
        executeUpdate(statement2, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), json);
    }

    private GameData findGame(int gameID){
        var statement = "SELECT gameID, whiteUsername, blackUsername, " +
                        "gameName, json FROM gameData WHERE gameID=?";
        return getGameInDatabase(statement, gameID);
    }

    private GameData getGameInDatabase(String statement, int gameID){
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: game not found", e);
        }
        throw new EmptyFieldException("Error: bad game ID");
    }
}
