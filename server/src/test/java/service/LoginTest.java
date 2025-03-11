package service;

import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {
    private static MySQLUserDAO userDAO;
    private static MySQLAuthDAO authDAO;
    private static UserService userService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @Order(1)
    public void testLoginUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);

        AuthData auth = userService.login("testUser", "password");

        assertNotNull(auth, "Login should return a valid AuthData.");
        assertEquals("testUser", auth.username(), "AuthData username should match logged-in user.");
        assertNotNull(auth.authToken(), "AuthData should contain a valid token.");
    }

    @Test
    @Order(2)
    public void testLoginUserNegative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);

        assertThrows(DataAccessException.class, () ->
                        userService.login("testUser", "wrongpassword"),
                "Logging in with the wrong password should fail.");
    }
}
