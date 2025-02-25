package server;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException{
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new DataAccessException("Bad Request");
        }
        if(userDAO.getUser(user.username())!= null){
            throw new DataAccessException("Username already taken");
        }

        userDAO.insertUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.insertAuth(auth);
        return auth;
    }
    public void logout(String authToken) throws DataAccessException{
        if(authDAO.getAuth(authToken) == null){
            throw new DataAccessException("Unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
