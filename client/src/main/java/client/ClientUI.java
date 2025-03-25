package client;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    private final ServerFacade serverFacade;
    private boolean isRunning = true;
    private boolean isLoggedIn = false;
    private String authToken;
    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();
    private final client.ChessboardDrawer chessboardDrawer = new client.ChessboardDrawer();
    public ClientUI(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Chess! Type 'help' for available commands.");

        while(isRunning){
            if(!isLoggedIn){
                System.out.print("> ");
                handlePreloginCommand(scanner.nextLine().trim().toLowerCase(),scanner);
            }else{
                System.out.print("> ");
                handlePostloginCommand(scanner.nextLine().trim().toLowerCase(), scanner);
            }
        }
        scanner.close();
    }

    private void handlePreloginCommand(String input, Scanner scanner) {
        switch (input) {
            case "help":
                displayPreloginHelp();
                break;
            case "quit":
                quit();
                break;
            case "login":
                login(scanner);
                break;
            case "register":
                register(scanner);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void handlePostloginCommand(String input, Scanner scanner) {
        switch (input) {
            case "help":
                displayPostloginHelp();
                break;
            case "logout":
                logout();
                break;
            case "create game":
                createGame(scanner);
                break;
            case "list games":
                listGames();
                break;
            case "join game":
                playGame(scanner);
                break;
            case "observe game":
                System.out.println("(Observe game logic coming soon!)");
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void  displayPreloginHelp(){
        System.out.println("Available commands:");
        System.out.println(" help");
        System.out.println(" quit");
        System.out.println(" login");
        System.out.println(" register");
    }

    private void displayPostloginHelp(){
        System.out.println("Available commands:");
        System.out.println(" help");
        System.out.println(" logout");
        System.out.println(" create game");
        System.out.println(" list games");
        System.out.println(" join game");
        System.out.println(" observe game");
    }

    private void quit(){
        System.out.println("Exiting Chess! Bye!");
        isRunning = false;
    }

    private void login(Scanner scanner){
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try{
            AuthData authData = serverFacade.login(username, password);
            this.authToken = authData.authToken();
            isLoggedIn = true;
            System.out.println("Login successful!");
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void register(Scanner scanner){
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        try{
            AuthData authData = serverFacade.register(username, password, email);
            this.authToken = authData.authToken();
            isLoggedIn = true;
            System.out.println("Registration Successful!");
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void logout(){
        try{
            serverFacade.logout(authToken);
            authToken = null;
            isLoggedIn = false;
            System.out.println("Logout Successful!");
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createGame(Scanner scanner){
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine().trim();

        try{
            serverFacade.createGame(authToken, gameName);
            System.out.println("Game Created successfully: "+ gameName);
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void playGame(Scanner scanner) {
        if (gameNumberToID.isEmpty()) {
            System.out.println("Fetching game list...");
            listGames();
        }

        System.out.print("Enter game number to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter team color (WHITE/BLACK): ");
        String teamColor = scanner.nextLine().trim().toUpperCase();

        Integer gameID = gameNumberToID.get(gameNumber);
        if (gameID == null) {
            System.out.println("Invalid game number. Please list games again.");
            return;
        }

        try {
            serverFacade.joinGame(authToken, gameID, teamColor);
            System.out.println("Joined game successfully as " + teamColor);
            boolean isWhite = teamColor.equalsIgnoreCase("WHITE");
            chessboardDrawer.drawChessboard(isWhite);
        } catch (Exception e) {
            if (e.getMessage().contains("Game is full")) {
                System.out.println("Error: Game is full. That color may already be taken.");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }




    private void listGames() {
        try {
            var games = serverFacade.listGames(authToken);
            gameNumberToID.clear();

            if (games.isEmpty()) {
                System.out.println("No games found.");
            } else {
                System.out.println("Games:");
                for (int i = 0; i < games.size(); i++) {
                    int gameNumber = i + 1;
                    var game = games.get(i);

                    gameNumberToID.put(gameNumber, game.gameID());

                    System.out.printf("%d. %s (White: %s, Black: %s)%n",
                            gameNumber,
                            game.gameName(),
                            game.whiteUsername() != null ? game.whiteUsername() : "-",
                            game.blackUsername() != null ? game.blackUsername() : "-"
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
