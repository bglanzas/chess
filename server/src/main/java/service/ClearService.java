package service;

import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;


public class ClearService {
    private final MySQLUserDAO userDAO;
    private final MySQLGameDAO gameDAO;
    private final MySQLAuthDAO authDAO;

    public ClearService(MySQLUserDAO userDAO, MySQLGameDAO gameDAO, MySQLAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clearDatabase() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
