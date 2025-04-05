package server;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import service.ClearService;
import websocket.GameWebSocketHandler;
import spark.Spark;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.webSocket("/ws", GameWebSocketHandler.class);
        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createDatabase();
            System.out.println("Database initialized successfully.");
        } catch (DataAccessException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return -1;
        }

        MySQLUserDAO userDAO = new MySQLUserDAO();
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        MySQLAuthDAO authDAO = new MySQLAuthDAO();

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(userDAO, authDAO);
        LoginHandler loginHandler = new LoginHandler(userDAO, authDAO);
        LogoutHandler logoutHandler = new LogoutHandler(authDAO);
        ListGameHandler listGameHandler = new ListGameHandler(gameDAO, authDAO);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameDAO, authDAO);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameDAO, authDAO);

        Spark.delete("/db", clearHandler.clearDatabase);
        Spark.post("/user", registerHandler.register);
        Spark.post("/session", loginHandler.login);
        Spark.delete("/session", logoutHandler.logout);
        Spark.get("/game", listGameHandler.listGame);
        Spark.post("/game", createGameHandler.createGame);
        Spark.put("/game", joinGameHandler.joinGame);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}


