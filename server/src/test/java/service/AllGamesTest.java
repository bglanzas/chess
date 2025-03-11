package service;

import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllGamesTest {
    private static MySQLUserDAO userDAO;
    private static MySQLAuthDAO authDAO;
    private static MySQLGameDAO gameDAO;
    private static UserService userService;
    private static GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    @Order(1)
    public void testListGamesPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");

        gameService.createGame(auth.authToken(), "Game1");
        gameService.createGame(auth.authToken(), "Game2");

        List<GameData> games = gameService.listGames(auth.authToken());

        assertNotNull(games, "List of games should not be null.");
        assertEquals(2, games.size(), "There should be exactly 2 games listed.");
    }

    @Test
    @Order(2)
    public void testListGamesNegative() {
        assertThrows(DataAccessException.class, () ->
                        gameService.listGames("invalidToken"),
                "Listing games with an invalid token should fail.");
    }

    @Test
    @Order(3)
    public void testJoinGamePositive() throws DataAccessException {
        UserData user = new UserData("player1", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("player1", "password");

        GameData game = gameService.createGame(auth.authToken(), "ChessMatch");

        gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");

        GameData updatedGame = gameDAO.getGame(game.gameID());
        assertEquals("player1", updatedGame.whiteUsername(), "White player should be set correctly.");
    }

    @Test
    @Order(4)
    public void testJoinGameNegative() throws DataAccessException {
        UserData user = new UserData("player3", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("player3", "password");

        assertThrows(DataAccessException.class, () ->
                        gameService.joinGame(auth.authToken(), 99999, "WHITE"),
                "Joining a non-existent game should fail.");
    }

    @Test
    @Order(5)
    public void testCreateGamePositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");

        GameData game = gameService.createGame(auth.authToken(), "TestGame");

        assertNotNull(game, "Game creation should return a valid GameData.");
        assertEquals("TestGame", game.gameName(), "Game name should match the created game.");
        assertTrue(game.gameID() > 0, "Game ID should be a positive integer.");
    }

    @Test
    @Order(6)
    public void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () ->
                        gameService.createGame("invalidToken", "InvalidGame"),
                "Creating a game with an invalid auth token should fail.");
    }
}

