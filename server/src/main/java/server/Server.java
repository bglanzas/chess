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

        Spark.delete("/db", clearHandler.clearDatabase);
        Spark.post("/user", registerHandler.register);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
