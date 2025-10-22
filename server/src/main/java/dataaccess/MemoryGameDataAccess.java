package dataaccess;

import chess.ChessGame;
import model.CreateGameResult;
import model.GameData;
import model.JoinGameRequest;
import service.AlreadyTakenException;
import service.EmptyFieldException;

import java.util.ArrayList;

public class MemoryGameDataAccess implements GameDataAccess{
    ArrayList<GameData> gameList = new ArrayList<>();
    Integer gameID = 1001;

    public void clear(){
        gameList.clear();
    }

    public ArrayList<GameData> listGames(){
        return gameList;
    }

    public CreateGameResult createGame(String gameName){
        int gameID = this.gameID;
        this.gameID += 1;
        var chess = new ChessGame();
        var gameData = new GameData(gameID, null, null, gameName, chess);
        gameList.add(gameData);
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest req, String username){
        var game = findGame(req.gameID());
        if (req.playerColor() == null){
            throw new EmptyFieldException("Error: PlayerColor field is empty");
        }
        if (!(req.playerColor().equals("WHITE") || req.playerColor().equals("BLACK"))) {
            throw new EmptyFieldException("Error: PlayerColor field is empty");
        }
        if (req.playerColor().equals("WHITE") && game.whiteUsername() == null) {
            var joinedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            gameList.remove(game);
            gameList.add(joinedGame);
        }
        else if (req.playerColor().equals("BLACK") && game.blackUsername() == null) {
            var joinedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            gameList.remove(game);
            gameList.add(joinedGame);
        }
        else {
            throw new AlreadyTakenException("Error: Selected color was already taken");
        }
    }

    private GameData findGame(int gameID) throws EmptyFieldException {
        for (GameData game : gameList){
            if (game.gameID() == gameID){
                return game;
            }
        }
        throw new EmptyFieldException("Error: Game does not exist");
    }

}
