package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in order
public class CreateGameTest {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserService userService;
    private static GameService gameService;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    @Order(1)
    public void testCreateGamePositive() throws DataAccessException {
        // Register and log in user
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");

        // Create game
        GameData game = gameService.createGame(auth.authToken(), "TestGame");

        assertNotNull(game, "Game creation should return a valid GameData.");
        assertEquals("TestGame", game.gameName(), "Game name should match the created game.");
        assertTrue(game.gameID() > 0, "Game ID should be a positive integer.");
    }


    @Test
    @Order(2)
    public void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () ->
                        gameService.createGame("invalidToken", "InvalidGame"),
                "Creating a game with an invalid auth token should fail.");
    }

}
