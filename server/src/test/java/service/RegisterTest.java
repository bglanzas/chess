package service;

import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterTest {
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
    public void testRegisterPositive() throws DataAccessException {
        userService.register(new UserData("testUser", "password", "email@example.com"));
        UserData user = userDAO.getUser("testUser");

        assertNotNull(user, "User should be registered successfully.");
        assertEquals("testUser", user.username(), "Username should match the registered user.");
        assertEquals("email@example.com", user.email(), "Email should match the registered user.");
    }

    @Test
    @Order(2)
    public void testRegisterNegative() {
        assertThrows(DataAccessException.class, () ->
                        userService.register(new UserData("", "password", "email@example.com")),
                "Registering a user without a username should fail.");
    }
}


