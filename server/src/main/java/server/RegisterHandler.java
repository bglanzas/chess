package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;


import java.util.Map;

public class RegisterHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    public Route register = (Request req, Response res) -> {
        UserData user = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData auth = userService.register(user);
            res.status(200);
            return gson.toJson(auth);
        } catch (DataAccessException e) {
            res.status(e.getMessage().contains("Username already taken") ? 403 : 400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    };
}
