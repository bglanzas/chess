package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;
import model.GameData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on port " + port);
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clearDatabase();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("user1", "pass1", "email1@example.com");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register("user1", "pass1", "email1@example.com");
        Exception e = assertThrows(Exception.class, () -> {
            facade.register("user1", "pass1", "email1@example.com");
        });
        assertTrue(e.getMessage().contains("Registration failed"));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("user2", "pass2", "email2@example.com");
        AuthData auth = facade.login("user2", "pass2");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
    }

    @Test
    public void loginNegative() {
        Exception e = assertThrows(Exception.class, () -> {
            facade.login("fakeuser", "fakepass");
        });
        assertTrue(e.getMessage().contains("Login failed"));
    }

    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register("user3", "pass3", "email3@example.com");
        GameData game = facade.createGame(auth.authToken(), "Cool Game");
        assertEquals("Cool Game", game.gameName());
    }

    @Test
    public void createGameNegative() {
        Exception e = assertThrows(Exception.class, () -> {
            facade.createGame("bad-token", "Should Fail");
        });
        assertTrue(e.getMessage().contains("Create game failed"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register("user4", "pass4", "email4@example.com");
        facade.createGame(auth.authToken(), "Game A");
        List<GameData> games = facade.listGames(auth.authToken());
        assertEquals(1, games.size());
    }

    @Test
    public void listGamesNegative() {
        Exception e = assertThrows(Exception.class, () -> {
            facade.listGames("bad-token");
        });
        assertTrue(e.getMessage().contains("Unable to list games"));
    }

    @Test
    public void joinGameNegative() throws Exception {
        AuthData auth = facade.register("user5", "pass5", "email5@example.com");
        Exception e = assertThrows(Exception.class, () -> {
            facade.joinGame(auth.authToken(), 99999, "WHITE");
        });
        assertTrue(e.getMessage().contains("Join game failed"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register("user6", "pass6", "email6@example.com");
        GameData game = facade.createGame(auth.authToken(), "Join Test Game");
        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), game.gameID(), "WHITE"));
    }

    @Test
    public void logoutPositive() throws Exception {
        AuthData auth = facade.register("user7", "pass7", "email7@example.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutNegative() {
        Exception e = assertThrows(Exception.class, () -> {
            facade.logout("invalid-token");
        });
        assertNotNull(e.getMessage());
    }
}





