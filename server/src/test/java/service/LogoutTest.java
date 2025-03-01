package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogoutTest {
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
    public void testLogoutUser_Positive() throws DataAccessException {
        // Register and login user
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");


        userService.logout(auth.authToken());


        assertNull(authDAO.getAuth(auth.authToken()), "Auth token should be removed after logout.");
    }


    @Test
    @Order(2)
    public void testLogoutUser_Negative_InvalidToken() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->
                        userService.logout("invalidToken"),
                "Logging out with an invalid token should fail.");
    }

}
