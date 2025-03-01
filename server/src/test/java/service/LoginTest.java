package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {
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
    public void testLoginUser_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);

        AuthData auth = userService.login("testUser", "password");

        assertNotNull(auth, "Login should return a valid AuthData.");
        assertEquals("testUser", auth.username(), "AuthData username should match logged-in user.");
        assertNotNull(auth.authToken(), "AuthData should contain a valid token.");
    }


    @Test
    @Order(2)
    public void testLoginUser_Negative_IncorrectPassword() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);

        assertThrows(DataAccessException.class, () ->
                        userService.login("testUser", "wrongpassword"),
                "Logging in with the wrong password should fail.");
    }

}

