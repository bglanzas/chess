package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import service.GameService;
import spark.*;

import java.util.Map;

public class JoinGameHandler {
    private GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameDAO gameDAO, AuthDAO authDAO){
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public Route joinGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        var json = gson.fromJson(req.body(), Map.class);

        try {
            if (!json.containsKey("gameID")) {
                throw new DataAccessException("Bad request");
            }

            Double gameIDDouble = (Double) json.get("gameID");
            if (gameIDDouble == null) {
                throw new DataAccessException("Bad request");
            }
            int gameID = gameIDDouble.intValue();


            String playerColor = (String) json.get("playerColor");
            if (playerColor == null || playerColor.isEmpty()) {
                throw new DataAccessException("Bad request");
            }

            gameService.joinGame(authToken, gameID, playerColor);

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(e.getMessage().equals("Unauthorized") ? 401 :
                    e.getMessage().equals("Bad request") ? 400 :
                            403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    };

}
