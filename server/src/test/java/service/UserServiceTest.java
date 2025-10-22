package service;

import model.LoginRequest;
import model.LogoutRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() {
        var userService = new UserService();
        var req = new RegisterRequest("mikedotcom", "1234", "m@gmoil.com");
        var res = userService.register(req);

        // Positive
        assertDoesNotThrow(() -> res);
        assertNotNull(res.authToken());


    }
    @Test
    void nRegister() {
        var userService = new UserService();

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
        assertEquals("mike", userService.login(req).username());

    }

    @Test
    void nLogin() {
        //Register a user, then logs out
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        userService.logout(new LogoutRequest(userService.getUserData().getAuthToken(registerReq.username())));

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

        // Positive
        var req = new LogoutRequest(authToken);
        assertEquals("{}", userService.logout(req));
    }

    @Test
    void nLogout() {
        //Register a user
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);

        // Negative
        var nAuthToken = "This is a token that will fail";
        var nReq = new LogoutRequest(nAuthToken);
        assertThrows(UnauthorizedException.class, () -> userService.logout(nReq));
    }


    @Test
    void verifyAuthData() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Positive
        assertDoesNotThrow(() -> userService.verifyAuthData(authToken));

    }

    @Test
    void nVerifyAuthData() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Negative
        assertThrows(UnauthorizedException.class, () -> userService.verifyAuthData("This is a fake authToken"));
    }

    @Test
    void getUsername() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Positive
        assertEquals("mike", userService.getUsername(authToken));
    }

    @Test
    void nGetUsername() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);

        // Negative
        assertNotEquals("mike", userService.getUsername("This is a fake token"));
    }

    @Test
    void getUserData() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Positive (shows data exists)
        assertEquals("mike", userService.getUserData().getUsername(authToken));

    }

    @Test
    void nGetUserData() {
        // Register User
        var userService = new UserService();
        var registerReq = new RegisterRequest("mike", "1234", "m@gmail.com");
        userService.register(registerReq);
        var authToken = userService.getUserData().getAuthToken(registerReq.username());

        // Negative (shows data is gone)
        assertNotEquals("joe", userService.getUserData().getUsername(authToken));
    }
}