package ui;

import chess.ChessMove;
import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import chess.ChessBoard;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import websocket.GameSocketClient;
import client.ChessboardDrawer;

import java.util.Scanner;

public class GameplayUI {
    private GameSocketClient wsClient;
    private final String authToken;
    private final int gameID;
    private ChessGame game;
    private boolean whitePerspective = true;
    private final ChessboardDrawer drawer = new ChessboardDrawer();
    private TeamColor playerTeam;

    public GameplayUI(GameSocketClient wsClient, String authToken, int gameID){
        this.wsClient = wsClient;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void setSocketClient(GameSocketClient client) {
        this.wsClient = client;
    }

    public void start(){
        wsClient.connect();
        wsClient.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));

        System.out.println("\n\u001B[1mEntered Gameplay! Type 'help' to see available commands.\u001B[0m");
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
                    return;
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

    public void setPlayerTeam(TeamColor team) {
        this.playerTeam = team;
    }


    private void drawBoard(){
        if (game != null){
            ChessBoard board = game.getBoard();
            drawer.drawChessboard(board, whitePerspective);
        } else {
            System.out.println("Game not loaded yet.");
        }
    }

    private void makeMove(Scanner scanner){
        try {
            System.out.print("Start (e.g. e2): ");
            String startInput = scanner.nextLine().trim().toLowerCase();

            System.out.print("End (e.g. e4): ");
            String endInput = scanner.nextLine().trim().toLowerCase();

            System.out.println("Parsing input: " + startInput + " to " + endInput);

            ChessPosition start = parsePosition(startInput);
            ChessPosition end = parsePosition(endInput);
            ChessMove move = new ChessMove(start, end, null);

            wsClient.send(new UserMoveCommand(authToken, gameID, move));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ChessPosition parsePosition(String input) {
        if (input.length() != 2) throw new IllegalArgumentException("Input length must be 2 (like 'e2')");

        char file = input.charAt(0);
        char rank = input.charAt(1);

        if (file < 'a' || file > 'h') throw new IllegalArgumentException("File must be between a and h");
        if (rank < '1' || rank > '8') throw new IllegalArgumentException("Rank must be between 1 and 8");

        int col = file - 'a' + 1;
        int row = rank - '0';

        return new ChessPosition(row, col);
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
                if (message instanceof LoadGameMessage loadGame) {
                    this.game = loadGame.getGame();
                    this.whitePerspective = (playerTeam == TeamColor.WHITE);
                    drawBoard();
                } else {
                    System.out.println("[Error] Invalid LOAD_GAME message received.");
                }
            }
            case NOTIFICATION -> {
                if (message instanceof NotificationMessage note) {
                    System.out.println("[Notification] " + note.getMessage());
                }
            }
            case ERROR -> {
                if (message instanceof ErrorMessage err) {
                    System.out.println("[Error] " + err.getErrorMessage());
                }
            }
        }
    }

}





