package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterTest {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @Order(1)
    public void testRegisterUser_Positive() throws DataAccessException {
        userService.register(new UserData("testUser", "password", "email@example.com")); // ✅ FIXED
        UserData user = userDAO.getUser("testUser");

        assertNotNull(user, "User should be registered successfully.");
        assertEquals("testUser", user.username(), "Username should match the registered user.");
        assertEquals("email@example.com", user.email(), "Email should match the registered user.");
    }


    @Test
    @Order(2)
    public void testRegisterUser_Negative() {
        assertThrows(DataAccessException.class, () ->
                        userService.register(new UserData("", "password", "email@example.com")), // ✅ FIXED
                "Registering a user without a username should fail.");
    }

}


