package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataAccess;
import model.LoginRequest;
import model.LogoutRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() {
        var userService = new UserService();

        // Positive
        var req = new RegisterRequest("mikedotcom", "1234", "m@gmoil.com");
        var res = userService.register(req);
        assertDoesNotThrow(() -> res);
        assertNotNull(res.authToken());

        // Negative
        var nReq = new RegisterRequest("dotcom", null, "m@gmoil.com");
        assertThrows(EmptyFieldException.class, () -> userService.register(nReq));
    }

    @Test
    void login() {
        //Register a user, then logs out
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        userService.logout(new LogoutRequest(userService.getUserData().getAuthToken(registerReq.username())));

        // Positive
        var req = new LoginRequest("mike", "1234");
        var res = userService.login(req);
        assertEquals("mike", res.username());

        // Negative (Wrong password)
        var nReq = new LoginRequest("mike", "123429");
        assertThrows(UnauthorizedException.class, () -> userService.login(nReq));
    }

    @Test
    void logout() {
        //Register a user
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Negative (Note, I did the negative test first so I wouldn't have to re-register before attempting another test)
        var nAuthToken = "This is a token that will fail";
        var nReq = new LogoutRequest(nAuthToken);
        assertThrows(UnauthorizedException.class, () -> userService.logout(nReq));

        // Positive
        var req = new LogoutRequest(authToken);
        assertEquals("{}", userService.logout(req));
    }
}