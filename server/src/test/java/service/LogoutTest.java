package service;

import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogoutTest {
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
    public void testLogoutUserPositive() throws DataAccessException {

        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");


        userService.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()), "Auth token should be removed after logout.");
    }

    @Test
    @Order(2)
    public void testLogoutUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->
                        userService.logout("invalidToken"),
                "Logging out with an invalid token should fail.");
    }
}

