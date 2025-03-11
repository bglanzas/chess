package dataaccess;

import model.UserData;
import java.util.List;

public interface UserDAOInterface {
    void clear() throws DataAccessException;
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}
