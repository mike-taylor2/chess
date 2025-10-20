package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataAccess;
import model.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() {
        var userService = new UserService();

        assertDoesNotThrow(() -> userService.register(new RegisterRequest("mikedotcom", "1234", "m@gmoil.com")));
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}