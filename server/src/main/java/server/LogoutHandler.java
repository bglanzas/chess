package server;

import com.google.gson.Gson;
import spark.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import service.UserService;

import java.util.Map;

public class LogoutHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(AuthDAO authDAO){
        this.userService = new UserService(null, authDAO);
    }

    public Route logout = (Request req, Response res) ->{
        String authToken = req.headers("Authorization");

        try{
            userService.logout(authToken);
            res.status(200);
            return  "{}";
        }catch(DataAccessException e){
            res.status(401);
            return  gson.toJson(Map.of("message", "Error UnAuthorized"));
        }
    };
}
