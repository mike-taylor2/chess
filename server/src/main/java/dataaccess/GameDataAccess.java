package dataaccess;

import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public interface GameDataAccess {
    void clear();
    ArrayList<GameData> listGames();

    CreateGameResult createGame(String gameName);
}
