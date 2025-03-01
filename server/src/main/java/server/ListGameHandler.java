package server;

import com.google.gson.Gson;
import spark.*;
import dataaccess.DataAccessException;
import service.GameService;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import model.GameData;

import java.util.Map;
import java.util.List;

public class ListGameHandler {
    private service.GameService gameService;
    private final Gson gson = new Gson();

    public ListGameHandler(GameDAO gameDAO, AuthDAO authDAO){
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public Route listGame = (Request req, Response res)->{
        String authToken = req.headers("Authorization");

        try{
           List<GameData> games = gameService.listGames(authToken);
           res.status(200);
           return gson.toJson(Map.of("games", games));
        }catch (DataAccessException e){
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }catch(Exception e){
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    };
}
