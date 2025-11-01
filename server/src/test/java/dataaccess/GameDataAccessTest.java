package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinGameRequest;
import model.RegisterRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.ResponseException;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameDataAccessTest {


    private GameDataAccess getDataAccess(Class<? extends GameDataAccess> databaseClass) throws ResponseException {
        GameDataAccess db;
        if (databaseClass.equals(MySqlGameDataAccess.class)) {
            db = new MySqlGameDataAccess();
        } else {
            db = new MemoryGameDataAccess();
        }
        db.clear();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDataAccess.class, MemoryGameDataAccess.class})
    void clear(Class<? extends GameDataAccess> dbClass) {

    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDataAccess.class, MemoryGameDataAccess.class})
    void listGames(Class<? extends GameDataAccess> dbClass) {
        var gameList = new ArrayList<>();
        var db = getDataAccess(dbClass);
        var game1 = new GameData(1001, null, null, "myGame1", new ChessGame());
        var game2 = new GameData(1002, null, null, "myGame2", new ChessGame());

        db.createGame("myGame1");
        db.createGame("myGame2");

        gameList.add(game1);
        gameList.add(game2);

        assertEquals(gameList, db.listGames());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDataAccess.class, MemoryGameDataAccess.class})
    void createGame(Class<? extends GameDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var game = new GameData(1001, null, null, "myGame", new ChessGame());

        assertDoesNotThrow(() -> db.createGame("myGame"));
        assertDoesNotThrow(() -> db.createGame("myGame2"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDataAccess.class, MemoryGameDataAccess.class})
    void joinGame(Class<? extends GameDataAccess> dbClass) {
        var userService = new UserService();
        var userDataAccess = userService.getUserData();
        userDataAccess.clear();
        var db = getDataAccess(dbClass);

        // Register 3 different users
        var registerReq1 = new RegisterRequest("mike", "1234", "m@ofidsjfo.com");
        userService.register(registerReq1);
        var username1 = registerReq1.username();
        var registerReq2 = new RegisterRequest("Joe", "1234", "m@ofidsjfo.com");
        userService.register(registerReq2);
        var username2 = registerReq2.username();
        var registerReq3 = new RegisterRequest("Ty", "1234", "m@ofidsjfo.com");
        userService.register(registerReq3);
        var username3 = registerReq3.username();

        var game = new GameData(1001, null, null, "myGame", new ChessGame());
        db.createGame("myGame");

        assertDoesNotThrow(() -> db.joinGame(new JoinGameRequest("WHITE", 1001), username1));
        assertThrows(Exception.class, () -> db.joinGame(new JoinGameRequest("WHITE", 1001), username2));
    }
}