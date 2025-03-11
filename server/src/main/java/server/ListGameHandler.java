package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import service.GameService;
import spark.*;

import java.util.List;
import java.util.Map;

public class ListGameHandler {
    private GameService gameService;  // Correctly initialized as 'final'
    private final Gson gson = new Gson();

    public ListGameHandler(MySQLGameDAO gameDAO, MySQLAuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public Route listGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");

        try {
            List<GameData> games = gameService.listGames(authToken);

            var gamesResponse = games.stream()
                    .map(game -> Map.of(
                            "gameID", game.gameID(),
                            "gameName", game.gameName(),
                            "whiteUsername", game.whiteUsername(),
                            "blackUsername", game.blackUsername(),
                            "gameState", game.game()
                    )).toList();

            res.status(200);
            return gson.toJson(Map.of("games", gamesResponse));

        } catch (DataAccessException e) {
            res.status(e.getMessage().equals("Unauthorized") ? 401 : 500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Internal server error"));
        }
    };
}
