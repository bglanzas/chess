package ui;

import chess.ChessMove;
import chess.ChessGame;
import chess.ChessPosition;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import client.websocket.GameSocketClient;

import java.net.http.WebSocket;
import java.util.Scanner;

public class GameplayUI {
    private final GameSocketClient wsClient;
    private final String authToken;
    private final int gameID;
    private ChessGame game;

    public GameplayUI(WebSocket wsClient, String authToken, int gameID){
        this.wsClient = wsClient;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void start(){
        wsClient.connect();
        wsClient.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));

        System.out.println("\n\u001B[1mEntered Gameplay! Type 'help' to see available commands.\\u001B[0m");
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("\n[gameplay] > ");
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input){
                case "help" -> printHelp();
                case "redraw" -> drawBoard();
                case "move" -> makeMove(scanner);
                case "highlight" -> highlightMoves(scanner);
                case "leave" -> {
                    wsClient.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
                    return;;
                }
                case "resign" -> {
                    System.out.println("Are you sure you want to resign? (y/n): ");
                    if(scanner.nextLine().trim().equalsIgnoreCase("y")){
                        wsClient.send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
                    }
                }
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  help        - Show available commands");
        System.out.println("  redraw      - Redraw the chess board");
        System.out.println("  move        - Make a move (e.g., 'move')");
        System.out.println("  highlight   - Highlight legal moves for a piece");
        System.out.println("  resign      - Resign from the game");
        System.out.println("  leave       - Leave the game");
    }

    private void drawBoard(){
        if (game != null){
            System.out.println("[Board Drawn Here");
        }else{
            System.out.println("Game not loaded yet.");
        }
    }

    private void makeMove(Scanner scanner){
        try{
            System.out.print("Start (row col): ");
            int r1 = scanner.nextInt();
            int c1 = scanner.nextInt();
            System.out.print("End (row col): ");
            int r2 = scanner.nextInt();
            int c2 = scanner.nextInt();
            scanner.nextLine();

            ChessPosition start = new ChessPosition(r1, c1);
            ChessPosition end = new ChessPosition(r2, c2);
            ChessMove move = new ChessMove(start, end, null);

            wsClient.send(new UserMoveCommand(authToken, gameID, move));
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
            scanner.nextLine();
        }
    }

    private void highlightMoves(Scanner scanner) {
        try {
            System.out.print("Enter position to highlight (row col): ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            scanner.nextLine();

            ChessPosition position = new ChessPosition(row, col);
            System.out.println("Highlighting legal moves from: " + position);
            if (game != null) {
                var moves = game.validMoves(position);
                for (var move : moves) {
                    System.out.println(" → " + move.getEndPosition());
                }
            } else {
                System.out.println("Game not loaded yet.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    public void onMessage(ServerMessage message){
        switch (message.getServerMessageType()){
            case LOAD_GAME -> {
                this.game = ((LoadGameMessage) message).getGame();
                drawBoard();
            }
            case NOTIFICATION -> System.out.println("[Notification] " + ((NotificationMessage) message).getMessage());
            case ERROR -> System.out.println("[Error] " + ((ErrorMessage) message).getErrorMessage());
        }
    }
}
