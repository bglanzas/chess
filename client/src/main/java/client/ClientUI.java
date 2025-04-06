package client;

import chess.ChessGame;
import chess.ChessBoard;
import model.AuthData;
import model.GameData;
import client.GameplayUI;
import websocket.GameSocketClient;
import client.ChessboardDrawer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    private final ServerFacade serverFacade;
    private boolean isRunning = true;
    private boolean isLoggedIn = false;
    private String authToken;
    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();
    private final ChessboardDrawer chessboardDrawer = new ChessboardDrawer();

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
            case "play game":
                playGame(scanner);
                break;
            case "observe game":
                observeGame(scanner);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void displayPreloginHelp(){
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
        System.out.println(" play game");
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
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
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
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
        }
    }

    private void logout(){
        try{
            serverFacade.logout(authToken);
            authToken = null;
            isLoggedIn = false;
            System.out.println("Logout Successful!");
        }catch(Exception e){
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
        }
    }

    private void createGame(Scanner scanner){
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine().trim();

        try{
            serverFacade.createGame(authToken, gameName);
            System.out.println("Game Created successfully: "+ gameName);
        }catch (Exception e){
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
        }
    }

    private void playGame(Scanner scanner) {
        listGames();
        System.out.print("Enter game number to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        Integer gameID = gameNumberToID.get(gameNumber);

        if (gameID == null) {
            System.out.println("Invalid game number.");
            return;
        }

        System.out.print("Enter team color to play as (WHITE/BLACK): ");
        String teamColor = scanner.nextLine().trim().toUpperCase();

        try {
            serverFacade.joinGame(authToken, gameID, teamColor);
            System.out.println("Joined game successfully as " + teamColor);

            String uri = "ws://localhost:8080/ws";
            var gameplayUI = new GameplayUI(null, authToken, gameID);

            gameplayUI.setPlayerTeam(teamColor.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);

            var wsClient = new GameSocketClient(uri, gameplayUI);
            gameplayUI.setSocketClient(wsClient);
            gameplayUI.start();
        } catch (Exception e) {
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
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
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
        }
    }

    private void observeGame(Scanner scanner) {
        if (gameNumberToID.isEmpty()) {
            System.out.println("Fetching game list...");
            listGames();
        }

        System.out.print("Enter game number to observe: ");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
            return;
        }
        Integer gameID = gameNumberToID.get(gameNumber);
        if (gameID == null) {
            System.out.println("Invalid game number. Please list games again.");
            return;
        }

        System.out.print("View from which perspective? (WHITE/BLACK): ");
        String perspective = scanner.nextLine().trim().toUpperCase();
        boolean whitePerspective = perspective.equals("WHITE");

        try {
            GameData game = serverFacade.observeGame(authToken, gameID, whitePerspective);
            System.out.printf("Now observing game '%s' (%d) from %s's perspective.%n",
                    game.gameName(), gameID, whitePerspective ? "White" : "Black");

            ChessBoard board = game.game().getBoard();
            chessboardDrawer.drawChessboard(board, whitePerspective);
        } catch (Exception e) {
            String msg = e.getMessage().replace("Error: ", "").trim();
            System.out.println("Error: " + msg);
        }
    }
}

