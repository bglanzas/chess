package client;

import java.util.Scanner;

public class ClientUI {

    private final ServerFacade serverFacade;
    private boolean isRunning = true;

    public ClientUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Chess! Type 'help' for available commands.");

        while (isRunning) {
            System.out.print("prelogin> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help":
                    displayHelp();
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

        scanner.close();
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help     - Show available commands");
        System.out.println("  quit     - Exit the program");
        System.out.println("  login    - Login to your Chess account");
        System.out.println("  register - Register a new Chess account");
    }

    private void quit() {
        System.out.println("Exiting Chess client. Goodbye!");
        isRunning = false;
    }

    private void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            serverFacade.login(username, password);
            System.out.println("Login successful!");
            // Transition to Postlogin UI (next stage)
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        try {
            serverFacade.register(username, password, email);
            System.out.println("Registration successful!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
