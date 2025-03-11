package dataaccess;

import model.UserData;
import java.sql.*;


public class MySQLUserDAO implements UserDAOInterface {
    @Override
    public void clear() throws DataAccessException {
        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String clearAuthTokens = "DELETE FROM AuthTokens";
        String clearGames = "DELETE FROM Games";
        String clearUsers = "DELETE FROM Users";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(disableFKChecks);
            stmt.executeUpdate(clearAuthTokens);
            stmt.executeUpdate(clearGames);
            stmt.executeUpdate(clearUsers);
            stmt.executeUpdate(enableFKChecks);

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Users table: " + e.getMessage());
        }
    }


    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
        return null;
    }


}




