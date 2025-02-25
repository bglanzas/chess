package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.List;

public interface DataAccessInterface {
    void clear() throws DataAccessException;
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws  DataAccessException;
    void insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws  DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void insertAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
