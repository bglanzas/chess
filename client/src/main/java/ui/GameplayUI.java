package client;

import chess.*;
import ui.EscapeSequences;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.*;
import websocket.GameSocketClient;
import chess.ChessGame.TeamColor;
import client.ChessboardDrawer;
import java.util.Collection;
import java.util.Scanner;

public class GameplayUI {
    private GameSocketClient wsClient;
    private final String authToken;
    private final int gameID;
    private ChessGame game;
    private boolean whitePerspective = true;
    private final ChessboardDrawer drawer = new ChessboardDrawer();
    private ChessGame.TeamColor playerTeam;

    public GameplayUI(GameSocketClient wsClient, String authToken, int gameID) {
        this.wsClient = wsClient;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void setSocketClient(GameSocketClient client) {
        this.wsClient = client;
    }

    public void setPlayerTeam(TeamColor team) {
        this.playerTeam = team;
    }

    public void start() {
        wsClient.connect();
        wsClient.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));

        System.out.println("\n\u001B[1mEntered Gameplay! Type 'help' to see available commands.\u001B[0m");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input) {
                case "help" -> printHelp();
                case "redraw" -> drawBoard();
                case "move" -> makeMove(scanner);
                case "highlight" -> highlightMoves(scanner);
                case "leave" -> {
                    wsClient.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
                    return;
                }
                case "resign" -> {
                    System.out.print("Are you sure you want to resign? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
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

    private void drawBoard() {
        if (game != null) {
            ChessBoard board = game.getBoard();
            drawer.drawChessboard(board, whitePerspective);
        } else {
            System.out.println("Game not loaded yet.");
        }
    }

    private void makeMove(Scanner scanner) {
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

    private void highlightMoves(Scanner scanner) {
        try {
            System.out.print("Enter position to highlight (e.g. e2): ");
            String input = scanner.nextLine().trim().toLowerCase();
            ChessPosition position = parsePosition(input);

            if (game != null) {
                Collection<ChessMove> moves = game.validMoves(position);
                Collection<ChessPosition> highlights = moves.stream().map(ChessMove::getEndPosition).toList();
                drawer.drawHighlightedBoard(game.getBoard(), whitePerspective, highlights, position);

            } else {
                System.out.println("Game not loaded yet.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private ChessPosition parsePosition(String input) {
        if (input.length() != 2) {throw new IllegalArgumentException("Input length must be 2 (like 'e2')");}

        char file = input.charAt(0);
        char rank = input.charAt(1);

        if (file < 'a' || file > 'h') {throw new IllegalArgumentException("Letter must be between a and h");}
        if (rank < '1' || rank > '8') {throw new IllegalArgumentException("Number be between 1 and 8");}

        int col = file - 'a' + 1;
        int row = rank - '0';

        return new ChessPosition(row, col);
    }

    public void onMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                if (message instanceof LoadGameMessage loadGame) {
                    this.game = loadGame.getGame();

                    if (playerTeam != null) {
                        this.whitePerspective = (playerTeam == TeamColor.WHITE);
                    }

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


    public void setWhitePerspective(boolean white) {
        this.whitePerspective = white;
    }

}
