package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties. " + ex.getMessage());
        }
    }

    static void createDatabase() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }

            conn = DriverManager.getConnection(CONNECTION_URL + "/" + DATABASE_NAME, USER, PASSWORD);

            var createUsersTable = """
            CREATE TABLE IF NOT EXISTS Users (
                username VARCHAR(50) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL
            )
            """;
            try (var stmt = conn.prepareStatement(createUsersTable)) {
                stmt.executeUpdate();
            }

            var createGamesTable = """
            CREATE TABLE IF NOT EXISTS Games (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(50),
                blackUsername VARCHAR(50),
                gameName VARCHAR(100) NOT NULL,
                gameState TEXT,
                FOREIGN KEY (whiteUsername) REFERENCES Users(username),
                FOREIGN KEY (blackUsername) REFERENCES Users(username)
            )
            """;
            try (var stmt = conn.prepareStatement(createGamesTable)) {
                stmt.executeUpdate();
            }

            var createAuthTable = """
            CREATE TABLE IF NOT EXISTS AuthTokens (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                FOREIGN KEY (username) REFERENCES Users(username)
            )
            """;
            try (var stmt = conn.prepareStatement(createAuthTable)) {
                stmt.executeUpdate();
            }



        } catch (SQLException e) {
            throw new DataAccessException("Error initializing database: " + e.getMessage());
        }
    }

    static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(CONNECTION_URL + "/" + DATABASE_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            createDatabase();
            System.out.println("✅ Database and tables created successfully!");
        } catch (DataAccessException e) {
            System.err.println("❌ Database creation failed: " + e.getMessage());
        }
    }
}
