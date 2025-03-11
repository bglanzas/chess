package server;

import com.google.gson.Gson;
import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;

import java.util.Map;

public class LoginHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(MySQLUserDAO userDAO, MySQLAuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    public Route login = (Request req, Response res) -> {
        UserData user = gson.fromJson(req.body(), UserData.class);

        try {
            AuthData auth = userService.login(user.username(), user.password());
            res.status(200);
            return gson.toJson(auth);
        } catch (DataAccessException e) {
            res.status(e.getMessage().contains("Unauthorized") ? 401 : 400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Internal Server Error: " + e.getMessage()));
        }
    };
}

