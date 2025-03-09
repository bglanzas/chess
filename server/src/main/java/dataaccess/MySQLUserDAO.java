package dataaccess;

import model.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQLUserDAO implements UserDAOInterface {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE Users")) {
                stmt.executeUpdate();
            }

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

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        String sql = "UPDATE Users SET password = ?, email = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.password());
            stmt.setString(2, user.email());
            stmt.setString(3, user.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        List<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving users: " + e.getMessage());
        }
        return users;
    }
}




