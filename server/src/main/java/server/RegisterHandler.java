package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLUserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;

import java.util.Map;

public class RegisterHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(MySQLUserDAO userDAO, MySQLAuthDAO authDAO) {
        this.userService = new UserService(new MySQLUserDAO(), new MySQLAuthDAO());
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

