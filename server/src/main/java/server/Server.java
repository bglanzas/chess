package server;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import service.ClearService;
import spark.Spark;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");


        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();


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
