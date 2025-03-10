package dataaccess;

import model.AuthData;
import java.sql.*;

public class MySQLAuthDAO implements AuthDAOInterface{

    @Override
    public void clear() throws DataAccessException{
        String sql = "Truncate Table AuthTokens";

        try(Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error clearing AuthTokens: " + e.getMessage());
        }
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException{
        String sql = "INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)";

        try(Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0){
                throw new DataAccessException("Auth Token was not added");
            }
        }catch (SQLException e){
            throw new DataAccessException("Error inserting auth token: "+ e.getMessage());
        }
    }



}
