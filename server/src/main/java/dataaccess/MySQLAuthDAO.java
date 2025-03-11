package dataaccess;

import model.AuthData;
import java.sql.*;

public class MySQLAuthDAO implements AuthDAOInterface{

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM AuthTokens")) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Games")) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users")) {
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
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

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM AuthTokens WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        String sql = "DELETE FROM AuthTokens WHERE authToken = ?";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, authToken);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0){
                throw new DataAccessException("Auth token not found");
            }
        }catch(SQLException e){
            throw new DataAccessException("Error deleting auth: "+ e.getMessage());
        }
    }


}
