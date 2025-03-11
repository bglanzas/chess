package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import service.UserService;
import spark.*;

import java.util.Map;


public class LoginHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(MySQLUserDAO userDAO, MySQLAuthDAO authDAO){
        this.userService = new UserService(userDAO, authDAO);
    }

    public Route login = (Request req, Response res) ->{
        var json = gson.fromJson(req.body(), Map.class);
        try{
            AuthData auth = userService.login((String) json.get("username"), (String) json.get("password"));
            res.status(200);
            return gson.toJson(auth);
        }catch(DataAccessException e){
            res.status(e.getMessage().equals("Bad request") ? 400 :401);
            return  gson.toJson(Map.of("message", "Error: "+ e.getMessage()));
        }
    };
}
