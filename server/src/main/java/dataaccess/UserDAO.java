package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    public void clear(){
        users.clear();
    }

    public  void insertUser(UserData user) throws DataAccessException{
        if (users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username){
        return users.get(username);
    }

}
