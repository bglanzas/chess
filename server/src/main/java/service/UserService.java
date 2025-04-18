package service;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final MySQLUserDAO userDAO;
    private final MySQLAuthDAO authDAO;

    public UserService(MySQLUserDAO userDAO, MySQLAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isEmpty()) {
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


    public AuthData login(String username, String password) throws DataAccessException {
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new DataAccessException("Bad request");
        }

        UserData user = userDAO.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("Unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authDAO.insertAuth(auth);

        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}


