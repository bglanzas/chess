package dataaccess;

import model.UserData;

import java.sql.*;

public abstract class MySQLUserDAO implements DataAccessInterface{

    @Override
    public void clear() throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            conn.createStatement().execute("TRUNCATE TABLE Users");
            conn.createStatement().execute("TRUNCATE TABLE Games");
            conn.createStatement().execute("TRUNCATE TABLE AuthTokens");
        }catch(SQLException e){
            throw new DataAccessException("Error clearing database tables" + e.getMessage());
        }
    }



}
