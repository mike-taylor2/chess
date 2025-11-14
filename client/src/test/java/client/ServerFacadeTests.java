package client;

import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() {
        RegisterRequest req = new RegisterRequest("mike", "1233", "lsdfj");
        assertDoesNotThrow(() -> client.register(req));

        assertThrows(Exception.class, () -> client.register(req));
    }

    @Test
    public void login() {

    }

    @Test
    public void joinGame() {

    }

    @Test
    public void listGames() {

    }

    @Test
    public void logout() {

    }

    @Test
    public void createGame() {

    }

}
