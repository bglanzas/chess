package client;

import model.AuthData;

import java.util.Scanner;

public class ClientUI {
    private final ServerFacade serverFacade;
    private boolean isRunning = true;
    private boolean isLoggedIn = false;
    private String authToken;

    public ClientUI(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Chess! Type 'help' for available commands.");

        while(isRunning){
            if(!isLoggedIn){
                System.out.print("prelogin> ");
                handlePreloginCommand(scanner.nextLine().trim().toLowerCase(),scanner);
            }else{
                System.out.print("postlogin> ");
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
        System.out.print("Enter game number to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter team color (WHITE/BLACK): ");
        String teamColor = scanner.nextLine().trim().toUpperCase();

        try {
            serverFacade.joinGame(authToken, gameNumber, teamColor);
            System.out.println("Joined game successfully as " + teamColor);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listGames(){
        try{
            var games = serverFacade.listGames(authToken);
            if(games.isEmpty()){
                System.out.println("No games Found");
            }else{
                System.out.println("Games:");
                for(int i = 0; i < games.size(); i++){
                    System.out.println((i+1) + ". " + games.get(i).gameName());
                }
            }
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
