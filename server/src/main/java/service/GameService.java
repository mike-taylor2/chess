package service;

import dataaccess.GameDataAccess;
import dataaccess.MemoryGameDataAccess;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesResult;

public class GameService {
    private final GameDataAccess gameData;

    public GameService() {
        this.gameData = new MemoryGameDataAccess();
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
}
