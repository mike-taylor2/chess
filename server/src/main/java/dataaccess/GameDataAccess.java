package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public interface GameDataAccess {
    void clear();
    ArrayList<GameData> listGames();
}
