package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDataAccess implements GameDataAccess{
    ArrayList<GameData> gameList = new ArrayList<>();

    public void clear(){
        gameList.clear();
    }

    public ArrayList<GameData> listGames(){
        return gameList;
    }

}
