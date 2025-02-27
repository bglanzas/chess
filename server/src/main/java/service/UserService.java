package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isEmpty() ||
                user.password() == null || user.password().isEmpty() ||
                user.email() == null || user.email().isEmpty()) {
            throw new DataAccessException("Bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Username already taken");
        }

        userDAO.insertUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.insertAuth(auth);
        return auth;
    }

}
