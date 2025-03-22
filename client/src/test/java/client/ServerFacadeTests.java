package client;


import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clearDatabase();
    }


    @Test
    void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void createGameSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame(authData.authToken(), "New Chess Game");
        assertNotNull(game);
        assertEquals("New Chess Game", game.gameName());
    }

    @Test
    void listGamesWithGames() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame(authData.authToken(), "First Game");
        facade.createGame(authData.authToken(), "Second Game");
        List<GameData> games = facade.listGames(authData.authToken());
        assertEquals(2, games.size());
    }


    @Test
    void registerDuplicateUsername() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Exception exception = assertThrows(Exception.class, () ->
                facade.register("player1", "password", "p1@email.com")
        );
        assertTrue(exception.getMessage().contains("Username already taken"));
    }

    @Test
    void loginInvalidCredentials() throws Exception {
        Exception exception = assertThrows(Exception.class, () ->
                facade.login("unknownUser", "wrongPassword")
        );
        assertTrue(exception.getMessage().contains("Unauthorized"));
    }

    @Test
    void createGameWithoutAuthToken() throws Exception {
        Exception exception = assertThrows(Exception.class, () ->
                facade.createGame("invalidToken", "Game Without Auth")
        );
        assertTrue(exception.getMessage().contains("Unauthorized"));
    }

    @Test
    void joinGameWithInvalidGameID() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Exception exception = assertThrows(Exception.class, () ->
                facade.joinGame(authData.authToken(), 9999, "WHITE")
        );
        assertTrue(exception.getMessage().contains("Bad request"));
    }

    @Test
    void listGamesWithInvalidToken() throws Exception {
        Exception exception = assertThrows(Exception.class, () ->
                facade.listGames("invalidToken")
        );
        assertTrue(exception.getMessage().contains("Unauthorized"));
    }
}




