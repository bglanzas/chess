package client;

public class Main {
    public static void main(String[] args) {
        int port = 8080; // Use the same port your server is running on
        ServerFacade serverFacade = new ServerFacade(port);
        ClientUI clientUI = new ClientUI(serverFacade);
        clientUI.start();
    }
}
