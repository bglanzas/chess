import server.Server;
import client.ClientUI;
import client.ServerFacade;

public class Main {
    public static void main(String[] args) {
        // Start the server
        Server server = new Server();
        int port = server.run(8080);
        System.out.println("Server started on port " + port);
        ServerFacade serverFacade = new ServerFacade(port);
        ClientUI clientUI = new ClientUI(serverFacade);


        clientUI.start();

        server.stop();
    }
}

