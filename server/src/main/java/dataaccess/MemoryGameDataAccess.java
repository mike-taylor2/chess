package dataaccess;

import chess.ChessGame;

import java.util.ArrayList;

public class MemoryGameDataAccess implements GameDataAccess{
    ArrayList<ChessGame> gameList = new ArrayList<>();

    public void clear(){
        gameList.clear();
    }

}
