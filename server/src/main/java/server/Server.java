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

        // ✅ Initialize DAO instances
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();

        // ✅ Initialize Service
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        // ✅ Initialize Handler
        ClearHandler clearHandler = new ClearHandler(clearService);

        // ✅ Register the /db DELETE endpoint
        Spark.delete("/db", clearHandler.clearDatabase);

        // ✅ Start Spark Server
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
