package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO implements GameDAOInterface{
    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException{
        String sql = "TRUNCATE TABLE Games";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.executeUpdate();
        }catch(SQLException e){
            throw new DataAccessException("Error clearing Games table: "+ e.getMessage());
        }
    }

    @Override
    public void insertGame(GameData game) throws DataAccessException{
        String sql = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, game) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, game.gameName());
            stmt.setString(5, gson.toJson(game.game()));

            stmt.executeUpdate();
        }catch(SQLException e){
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gson.fromJson(rs.getString("game"), ChessGame.class)  // Deserialize game
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, gson.toJson(game.game())); // Serialize the chess game
            stmt.setInt(4, game.gameID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Game not found for update.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gson.fromJson(rs.getString("game"), ChessGame.class)
                );
                games.add(game);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }

        return games;
    }


}
