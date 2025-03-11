package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import chess.ChessPosition;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MySQLGameDAOTest {
    private static MySQLGameDAO gameDAO;
    private static MySQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        userDAO = new MySQLUserDAO();

        gameDAO.clear();
        userDAO.clear();

        UserData whiteUser = new UserData("whiteUser", "password", "white@example.com");
        UserData blackUser = new UserData("blackUser", "password", "black@example.com");

        userDAO.insertUser(whiteUser);
        userDAO.insertUser(blackUser);
    }


    @Test
    @Order(1)
    public void testInsertGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();  // Example chess game state
        GameData game = new GameData(0, "whiteUser", "blackUser", "Epic Chess Battle", chessGame);

        GameData insertedGame = gameDAO.insertGame(game);

        assertNotNull(insertedGame, "Game should be successfully inserted.");
        assertTrue(insertedGame.gameID() > 0, "Game ID should be auto-generated and positive.");
        assertEquals("Epic Chess Battle", insertedGame.gameName(), "Game name should match.");
    }

    @Test
    @Order(2)
    public void testInsertGameNegative() {
        ChessGame chessGame = new ChessGame();
        GameData invalidGame = new GameData(0, "fakeUser", "ghostUser", "Invalid Game", chessGame);

        assertThrows(DataAccessException.class, () -> gameDAO.insertGame(invalidGame),
                "Inserting a game with invalid users should fail.");
    }


    @Test
    @Order(3)
    public void testGetGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Retrieve Me", chessGame);
        GameData insertedGame = gameDAO.insertGame(game);

        GameData retrievedGame = gameDAO.getGame(insertedGame.gameID());

        assertNotNull(retrievedGame, "Game should be retrieved successfully.");
        assertEquals("Retrieve Me", retrievedGame.gameName(), "Game name should match.");
    }

    @Test
    @Order(4)
    public void testGetGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(99999), "Fetching non-existent game should return null.");
    }

    @Test
    @Order(5)
    public void testListGamesPositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Chess Showdown", new ChessGame()));
        gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Ultimate Battle", new ChessGame()));

        List<GameData> games = gameDAO.listGames();

        assertNotNull(games, "List of games should not be null.");
        assertEquals(2, games.size(), "Two games should be listed.");
    }

    @Test
    @Order(6)
    public void testListGamesNegative() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "List should be empty if no games exist.");
    }

    @Test
    @Order(7)
    public void testUpdateGamePositive() throws DataAccessException, InvalidMoveException {
        ChessGame initialState = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Initial Game", initialState);
        GameData insertedGame = gameDAO.insertGame(game);

        GameData retrievedGame = gameDAO.getGame(insertedGame.gameID());
        assertNotNull(retrievedGame, "Game should exist after insertion.");

        ChessMove sampleMove = new ChessMove(
                new ChessPosition(2, 1),
                new ChessPosition(3, 1),
                null
        );

        ChessGame updatedState = retrievedGame.game();
        updatedState.makeMove(sampleMove);

        GameData updatedGame = new GameData(
                insertedGame.gameID(),
                retrievedGame.whiteUsername(),
                retrievedGame.blackUsername(),
                "Updated Game",
                updatedState
        );

        gameDAO.updateGame(updatedGame);

        GameData finalGame = gameDAO.getGame(insertedGame.gameID());
        assertNotNull(finalGame, "Game should still exist after the update.");
        assertEquals("Updated Game", finalGame.gameName(), "Game name should be updated.");
        assertEquals(updatedState, finalGame.game(), "Game state should match the updated state.");
    }

    @Test
    @Order(8)
    public void testUpdateGameNegative() throws DataAccessException {
        ChessGame invalidState = new ChessGame();
        GameData invalidGame = new GameData(99999, "fakeUser", "ghostUser", "Invalid Game", invalidState);

        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(invalidGame),
                "Updating a non-existent game should fail.");
    }


    @Test
    @Order(9)
    public void testClearPositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Test Game", new ChessGame()));
        gameDAO.clear();

        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "All games should be cleared.");
    }

}






