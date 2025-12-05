package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.GameDataAccess;
import dataaccess.MySqlGameDataAccess;
import model.*;

public class GameService {
    private final GameDataAccess gameData;

    public GameService() {
        this.gameData = new MySqlGameDataAccess();
    }

    public GameDataAccess getGameData(){
        return gameData;
    }

    public ListGamesResult listGames(){
        return new ListGamesResult(gameData.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest req) throws EmptyFieldException {
        if (req.gameName() == null || req.gameName().isEmpty()){
            throw new EmptyFieldException("Error: must include game name");
        }
        return gameData.createGame(req.gameName());
    }

    public String joinGame(JoinGameRequest req, String username){
        gameData.joinGame(req, username);
        return "{}";
    }

    public boolean verifyGameID(int gameID) {
        return gameData.verifyGameID(gameID);
    }

    public ChessGame makeMove(int gameID, ChessMove move) throws InvalidMoveException {
        return gameData.makeMove(gameID, move);
    }

    public void finishGame(int gameID) {
        gameData.finishGame(gameID);
    }

    public boolean checkFinishedGame(int gameID) {
        return gameData.checkFinishedGame(gameID);
    }

    public void leaveGame(int gameID, String username) {
        gameData.leaveGame(gameID, username);
    }
}
