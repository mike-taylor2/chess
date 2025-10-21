package dataaccess;

import chess.ChessGame;
import model.CreateGameResult;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDataAccess implements GameDataAccess{
    ArrayList<GameData> gameList = new ArrayList<>();
    Integer gameID = 1000;

    public void clear(){
        gameList.clear();
    }

    public ArrayList<GameData> listGames(){
        return gameList;
    }

    public CreateGameResult createGame(String gameName){
        int gameID = this.gameID + 1;
        var chess = new ChessGame();
        var gameData = new GameData(gameID, null, null, gameName, chess);
        gameList.add(gameData);
        return new CreateGameResult(gameID);
    }

}
