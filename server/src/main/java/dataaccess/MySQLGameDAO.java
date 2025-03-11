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
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Games table: " + e.getMessage());
        }
    }



    @Override
    public GameData insertGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Gson gson = new Gson();
            String gameStateJson = gson.toJson(game.game());

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gameStateJson);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Failed to insert game.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedGameID = generatedKeys.getInt(1);  // Retrieve auto-generated game ID
                    return new GameData(generatedGameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new DataAccessException("Failed to retrieve generated game ID.");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }
    }




    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = """
            SELECT gameID, whiteUsername, blackUsername, gameName, gameState
            FROM Games
            WHERE gameID = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String gameStateJson = rs.getString("gameState");

                    ChessGame gameState = new Gson().fromJson(gameStateJson, ChessGame.class);

                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gameState
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
        UPDATE Games
        SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ?
        WHERE gameID = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, new Gson().toJson(game.game()));  // Correct JSON format for ChessGame
            stmt.setInt(5, game.gameID());

            if (stmt.executeUpdate() == 0) {
                throw new DataAccessException("Error updating game: No game found with provided ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }



    @Override
    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<GameData> games = new ArrayList<>();

            while (rs.next()) {
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("gameState") != null ?
                                gson.fromJson(rs.getString("gameState"), ChessGame.class) : null
                );
                games.add(game);
            }

            return games;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving games: " + e.getMessage());
        }
    }



}
