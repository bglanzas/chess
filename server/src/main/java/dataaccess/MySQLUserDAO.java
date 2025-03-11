package dataaccess;
import org.mindrot.jbcrypt.BCrypt;
import model.UserData;
import java.sql.*;


public class MySQLUserDAO implements UserDAOInterface {
    @Override
    public void clear() throws DataAccessException {
        String clearAuthTokens = "DELETE FROM AuthTokens";
        String clearGames = "DELETE FROM Games";
        String clearUsers = "DELETE FROM Users";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(clearAuthTokens);
            stmt.executeUpdate(clearGames);
            stmt.executeUpdate(clearUsers);


        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Users table: " + e.getMessage());
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isEmpty() ||
                user.password() == null || user.password().isEmpty() ||
                user.email() == null || user.email().isEmpty()) {

            throw new DataAccessException("Invalid user data: Missing fields");
        }

        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
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




