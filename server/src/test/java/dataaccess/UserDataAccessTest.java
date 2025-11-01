package dataaccess;

import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

class UserDataAccessTest {

    private UserDataAccess getDataAccess(Class<? extends UserDataAccess> databaseClass) throws ResponseException {
        UserDataAccess db;
        if (databaseClass.equals(MySqlUserDataAccess.class)) {
            db = new MySqlUserDataAccess();
        } else {
            db = new MemoryUserDataAccess();
        }
        db.clear();
        return db;
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void createUser(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        assertDoesNotThrow(() -> db.createUser(user));

        var badUser = new UserData("mikedotcom", null, "kdjsl@gmail.com");
        assertThrows(Exception.class, () -> db.createUser(badUser));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void verifyAuthData(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        db.createUser(user);
        var authToken = db.getAuthToken(user.username());

        assertTrue(db.verifyAuthData(authToken));

        assertFalse(db.verifyAuthData("bad username"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void loginUser(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");

        // Creates a user then logs out
        db.createUser(user);
        var authToken = db.getAuthToken(user.username());
        db.deleteAuthToken(authToken);

        assertDoesNotThrow(() -> db.loginUser(user.username(), user.password()));
        assertThrows(Exception.class, () -> db.loginUser("non-existent user", null));

    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void deleteAuthToken(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        db.createUser(user);
        var authToken = db.getAuthToken(user.username());

        assertDoesNotThrow(() -> db.deleteAuthToken(authToken));
        assertThrows(Exception.class, () -> db.deleteAuthToken("Fake authToken"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void getUsername(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        db.createUser(user);
        var authToken = db.getAuthToken(user.username());

        assertDoesNotThrow(() -> db.getUsername(authToken));
        assertNull(db.getUsername("fake authToken"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void getAuthToken(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        db.createUser(user);

        assertDoesNotThrow(() -> db.getAuthToken(user.username()));
        assertNull(db.getAuthToken("fake username"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDataAccess.class, MemoryUserDataAccess.class})
    void clear(Class<? extends UserDataAccess> dbClass) {
        var db = getDataAccess(dbClass);
        var user = new UserData("mikedotcom", "1234", "j@gmail.com");
        db.createUser(user);
        db.clear();

        assertNull(db.getAuthToken(user.username()));
    }
}