package server;

import com.google.gson.Gson;
import service.ClearService;
import spark.*;
import dataaccess.DataAccessException;

import java.util.Map;

public class ClearHandler {
    private ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Route clearDatabase = (Request req, Response res) -> {
        try {
            clearService.clearDatabase();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    };
}

