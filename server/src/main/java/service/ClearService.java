package service;

import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

public class ClearService {
    private final UserDataAccess userData;
    private final GameDataAccess gameData;

    public ClearService(UserService user, GameService game){
        this.userData = user.getUserData();
        this.gameData = game.getGameData();
    }

    public String clear(){
        userData.clear();
        gameData.clear();
        return "{}";
    }
}
