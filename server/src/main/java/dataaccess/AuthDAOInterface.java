package dataaccess;

import model.AuthData;


public interface AuthDAOInterface {
    void clear() throws DataAccessException;
    void insertAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}