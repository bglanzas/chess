package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MySQLGameDAOTest {

    private MySQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void insertGame_positive() throws DataAccessException {
        GameData game = new GameData(1, "johnDoe", "janeDoe", "Chess Game", null);
        gameDAO.insertGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("Chess Game", retrievedGame.gameName());
    }

    @Test
    void getGame_negative_notFound() throws DataAccessException {
        assertNull(gameDAO.getGame(99)); // Game ID 99 shouldn't exist
    }

    @Test
    void updateGame_positive() throws DataAccessException {
        GameData game = new GameData(2, "johnDoe", "janeDoe", "Updated Game", null);
        gameDAO.insertGame(game);

        GameData updatedGame = new GameData(2, "johnDoe", "NEW PLAYER", "Updated Game", null);
        gameDAO.updateGame(updatedGame);

        GameData retrievedGame = gameDAO.getGame(2);
        assertEquals("NEW PLAYER", retrievedGame.blackUsername());
    }

    @Test
    void listGames_positive() throws DataAccessException {
        gameDAO.insertGame(new GameData(3, "playerA", "playerB", "Game A", null));
        gameDAO.insertGame(new GameData(4, "playerC", "playerD", "Game B", null));

        assertEquals(2, gameDAO.listGames().size());
    }
}
