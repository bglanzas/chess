package server;

import com.google.gson.Gson;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.*;

import java.util.Map;

public class CreateGameHandler {
    private GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(MySQLGameDAO gameDAO, MySQLAuthDAO authDAO) {
        this.gameService = new GameService(new MySQLGameDAO(), new MySQLAuthDAO());
    }

    public Route createGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        var json = gson.fromJson(req.body(), Map.class);

        try {
            String gameName = (String) json.get("gameName");
            GameData game = gameService.createGame(authToken, gameName);
            res.status(200);
            return gson.toJson(Map.of("gameID", game.gameID()));
        } catch (DataAccessException e) {
            res.status(e.getMessage().equals("Unauthorized") ? 401 : 400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    };
}

