package client;

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
}
