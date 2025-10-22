package service;

import model.CreateGameRequest;
import model.LogoutRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    @Test
    void clear() {
        var userService = new UserService();
        var gameService = new GameService();
        var clearService = new ClearService(userService, gameService);

        // Register a user
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Clear Memory
        clearService.clear();

        assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest(authToken)));

    }
}