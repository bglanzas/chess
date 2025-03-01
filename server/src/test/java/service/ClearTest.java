package service;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Run tests in order
public class ClearTest {
    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        authDAO = new AuthDAO();

        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @Order(1)
    public void testClearPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("testUser", "password", "email@example.com"));
        gameDAO.insertGame(new GameData(0, null, null, "Chess Game", null));

        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();

        assertNull(userDAO.getUser("testUser"));
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @Order(2)
    public void testClearNegative() throws DataAccessException {
        assertDoesNotThrow(() -> {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
        });
    }
}

