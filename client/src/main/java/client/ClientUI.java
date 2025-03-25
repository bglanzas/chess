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
                System.out.println("(Play game logic coming soon!)");
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


}
