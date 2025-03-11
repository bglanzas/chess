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
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGameHandler(MySQLGameDAO gameDAO, MySQLAuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public Route listGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");

        try {
            List<GameData> games = GameService.listGames(authToken);
            res.status(200);
            return gson.toJson(Map.of("games", games));
        } catch (DataAccessException e) {
            res.status(e.getMessage().equals("Unauthorized") ? 401 : 500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Internal server error"));
        }
    };
}
