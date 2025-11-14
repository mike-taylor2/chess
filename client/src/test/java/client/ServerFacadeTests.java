package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import facade.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        client = new ServerFacade(url);
    }

    @BeforeEach
    public void repeatClear() {
        try {
            client.clear();
        }
        catch (Exception e) {
            return;
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() {
        RegisterRequest req = new RegisterRequest("mike", "1233", "l");
        assertDoesNotThrow(() -> client.register(req));

        assertThrows(Exception.class, () -> client.register(req));
    }

    @Test
    public void login() {
        RegisterRequest req = new RegisterRequest("m", "1234", "l");
        LoginRequest loginReq = new LoginRequest("m", "1234");
        assertThrows(Exception.class, () -> client.login(loginReq));
        assertDoesNotThrow(() -> client.register(req));

        assertDoesNotThrow(() -> client.login(loginReq));
        assertDoesNotThrow(() -> client.logout());
    }

    @Test
    public void joinGame() {

        var registerReq1 = new RegisterRequest("mike", "1234", "m@ofidsjfo.com");
        assertThrows(Exception.class, () -> client.joinGame(new JoinGameRequest(null, 1234)));
        assertDoesNotThrow(() -> client.register(registerReq1));

        var game = new CreateGameRequest("test");

        int id;

        try {
            id = client.createGame(game).gameID();
        }
        catch (Exception e) {
            return;
        }

        var join = new JoinGameRequest("WHITE", id);
        assertDoesNotThrow(() -> client.joinGame(join));
    }

    @Test
    public void listGames() {
        var game1 = new CreateGameRequest("test1");
        var game2 = new CreateGameRequest("test2");
        var game3 = new CreateGameRequest("test3");

        var req = new RegisterRequest("Anna", "123", "lsd@.com");

        assertThrows(Exception.class, () -> client.listGames());

        assertThrows(Exception.class, () -> client.createGame(game1));

        assertDoesNotThrow(() -> client.register(req));
        assertDoesNotThrow(() -> client.login(new LoginRequest("Anna", "123")));

        assertDoesNotThrow(() -> client.createGame(game1));
        assertDoesNotThrow(() -> client.createGame(game2));
        assertDoesNotThrow(() -> client.createGame(game3));

        assertDoesNotThrow(() -> client.listGames());
    }

    @Test
    public void logout() {
        assertThrows(Exception.class, () -> client.logout());

        RegisterRequest req = new RegisterRequest("m", "1234", "l");
        assertDoesNotThrow(() -> client.register(req));
        assertDoesNotThrow(() -> client.login(new LoginRequest("m", "1234")));
        assertDoesNotThrow(() -> client.logout());
    }

    @Test
    public void createGame() {
        assertThrows(Exception.class, () -> client.createGame(new CreateGameRequest("game")));
        var req = new RegisterRequest("Anna", "123", "lsd@.com");
        assertDoesNotThrow(() -> client.register(req));
        assertDoesNotThrow(() -> client.login(new LoginRequest("Anna", "123")));

        assertDoesNotThrow(() -> client.createGame(new CreateGameRequest("game")));
    }

    @Test
    public void clear() {
        assertDoesNotThrow(() -> client.clear());

        assertDoesNotThrow(() -> client.clear());
    }

}
