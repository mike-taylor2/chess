package service;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @BeforeEach
    public void setup(){
        var user = new UserService();
        var game = new GameService();
        var clearService = new ClearService(user, game);
        clearService.clear();
    }

    @Test
    void createGame() {
        var gameService = new GameService();

        // Positive
        assertEquals(1001, gameService.createGame(new CreateGameRequest("myGame")).gameID());

        // Negative
        assertThrows(EmptyFieldException.class, () -> gameService.createGame(new CreateGameRequest(null)));
    }

    @Test
    void listGames() {
        var gameService = new GameService();

        gameService.createGame(new CreateGameRequest("myGame1"));
        gameService.createGame(new CreateGameRequest("myGame2"));
        gameService.createGame(new CreateGameRequest("myGame3"));

        var game1 = new GameData(1001, null, null, "myGame1", new ChessGame());
        var game2 = new GameData(1002, null, null, "myGame2", new ChessGame());
        var game3 = new GameData(1003, null, null, "myGame3", new ChessGame());

        ArrayList<GameData> gameList = new ArrayList<>();
        gameList.add(game1);
        gameList.add(game2);
        gameList.add(game3);

        // Positive
        assertEquals(gameService.listGames(), new ListGamesResult(gameList));

        // Negative
        var fakeGame = new GameData(1000001, null, null, "fakeGame", new ChessGame());
        gameList.add(fakeGame);
        assertNotEquals(gameService.listGames(), new ListGamesResult(gameList));
    }

    @Test
    void joinGame() {
        var userService = new UserService();
        var gameService = new GameService();

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

        //Create Game
        var game = gameService.createGame(new CreateGameRequest("myGame"));

        // Positive test
        assertEquals("{}", gameService.joinGame(new JoinGameRequest("WHITE", 1001), username1));
        assertEquals("{}", gameService.joinGame(new JoinGameRequest("BLACK", 1001), username2));

        // Negative test
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(new JoinGameRequest("WHITE", 1001), username3));
    }

    @Test
    void getGameData() {
        var userService = new UserService();
        var gameService = new GameService();

        // Register User
        var registerReq = new RegisterRequest("mike", "1234", "m@ofidsjfo.com");
        userService.register(registerReq);

        //Create Game in Server
        var game = gameService.createGame(new CreateGameRequest("myGame"));

        //Create Game to test locally
        var game1 = new GameData(1001, null, null, "myGame", new ChessGame());
        ArrayList<GameData> gameList = new ArrayList<>();
        gameList.add(game1);

        // Positive test
        assertEquals(gameList, gameService.getGameData().listGames());

        gameService.getGameData().clear();

        // Negative test: Shows data is gone
        assertNotEquals(gameList, gameService.getGameData().listGames());
    }
}