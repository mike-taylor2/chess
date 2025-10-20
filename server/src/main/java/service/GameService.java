package service;

import chess.ChessGame;
import dataaccess.GameDataAccess;
import dataaccess.MemoryGameDataAccess;
import dataaccess.MemoryUserDataAccess;
import dataaccess.UserDataAccess;

import java.util.ArrayList;

public class GameService {
    private final GameDataAccess dataAccess;

    public GameService() {
        this.dataAccess = new MemoryGameDataAccess();
    }

    GameDataAccess getDataAccess(){
        return dataAccess;
    }
}
