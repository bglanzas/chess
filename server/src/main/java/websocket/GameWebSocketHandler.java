package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;

import java.io.IOException;
import java.util.*;

@WebSocket
public class GameWebSocketHandler {
    private static final Map<Integer, Set<Session>> gameSessions = new HashMap<>();
    private static final Map<Session, Integer> sessionToGameID = new HashMap<>();
    private static final Map<Session, String> sessionToUsername = new HashMap<>();
    private final Set<Integer> resignedGames = new HashSet<>();
    private final Gson gson = new Gson();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();

    @OnWebSocketConnect
    public void onConnect(Session session) {

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = sessionToGameID.remove(session);
        String username = sessionToUsername.remove(session);

        if (gameID != null) {
            gameSessions.getOrDefault(gameID, new HashSet<>()).remove(session);
            broadcast(gameID, new NotificationMessage(username + " disconnected."));
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
            }
        } catch (Exception e) {
            send(session, new ErrorMessage("Error: Invalid command format or unexpected error."));
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws DataAccessException, IOException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            send(session, new ErrorMessage("Error: Unauthorized"));
            return;
        }

        GameData game = gameDAO.getGame(command.getGameID());
        if (game == null) {
            send(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        if (game.game() == null) {
            ChessGame newGame = new ChessGame();
            game = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    newGame
            );
            gameDAO.updateGame(game);
        }

        gameSessions.computeIfAbsent(game.gameID(), k -> new HashSet<>()).add(session);
        sessionToGameID.put(session, game.gameID());
        sessionToUsername.put(session, auth.username());

        send(session, new LoadGameMessage(game.game()));

        String username = auth.username();
        String role;
        if (username.equals(game.whiteUsername())) {
            role = "white";
        } else if (username.equals(game.blackUsername())) {
            role = "black";
        } else {
            role = "observer";
        }

        String message = switch (role) {
            case "white" -> username + " joined the game as white.";
            case "black" -> username + " joined the game as black.";
            default -> username + " joined the game as an observer.";
        };

        broadcast(game.gameID(), new NotificationMessage(message));
    }



    private void handleMakeMove(Session session, UserGameCommand command) throws DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            send(session, new ErrorMessage("Error: Unauthorized"));
            return;
        }

        GameData game = gameDAO.getGame(command.getGameID());
        if (game == null) {
            send(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        if (resignedGames.contains(game.gameID())) {
            send(session, new ErrorMessage("Error: Game is over. No moves allowed."));
            return;
        }

        ChessGame chessGame = game.game();
        ChessMove move = command.getMove();

        String username = sessionToUsername.get(session);
        ChessGame.TeamColor playerColor =
                username.equals(game.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                        username.equals(game.blackUsername()) ? ChessGame.TeamColor.BLACK : null;

        if (playerColor == null) {
            send(session, new ErrorMessage("Error: Only white or black players can make moves."));
            return;
        }

        if (playerColor != chessGame.getTeamTurn()) {
            send(session, new ErrorMessage("Error: It's not your turn."));
            return;
        }

        try {
            chessGame.makeMove(move);
        } catch (Exception e) {
            send(session, new ErrorMessage("Error: " + e.getMessage()));
            return;
        }

        gameDAO.updateGame(game);

        broadcast(game.gameID(), new LoadGameMessage(chessGame));
        broadcastExcept(session, game.gameID(), new NotificationMessage(username + " moved: " + move));

        if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
            broadcast(game.gameID(), new NotificationMessage("Checkmate! " + chessGame.getTeamTurn() + " is in checkmate."));
        } else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
            broadcast(game.gameID(), new NotificationMessage(chessGame.getTeamTurn() + " is in check."));
        } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
            broadcast(game.gameID(), new NotificationMessage("Stalemate! The game is a draw."));
        }
    }



    private void handleLeave(Session session, UserGameCommand command) throws DataAccessException {
        Integer gameID = sessionToGameID.remove(session);
        String username = sessionToUsername.remove(session);
        if (gameID != null && username != null) {
            gameSessions.getOrDefault(gameID, new HashSet<>()).remove(session);

            GameData game = gameDAO.getGame(gameID);
            if (game != null) {
                String white = game.whiteUsername();
                String black = game.blackUsername();
                boolean changed = false;

                if (username.equals(white)) {
                    game = new GameData(gameID, null, black, game.gameName(), game.game());
                    changed = true;
                } else if (username.equals(black)) {
                    game = new GameData(gameID, white, null, game.gameName(), game.game());
                    changed = true;
                }

                if (changed) {
                    gameDAO.updateGame(game);
                }
            }

            broadcast(gameID, new NotificationMessage(username + " left the game."));
        }
    }


    private void handleResign(Session session, UserGameCommand command) throws DataAccessException {
        GameData game = gameDAO.getGame(command.getGameID());
        if (game == null) {
            send(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        String username = sessionToUsername.get(session);
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            send(session, new ErrorMessage("Error: Only players can resign."));
            return;
        }

        if (resignedGames.contains(game.gameID())) {
            send(session, new ErrorMessage("Error: Game is already over."));
            return;
        }

        resignedGames.add(game.gameID());

        broadcast(game.gameID(), new NotificationMessage(username + " resigned. Game over."));
        gameDAO.updateGame(game);
    }

    private void send(Session session, ServerMessage message) {
        try {
            session.getRemote().sendString(gson.toJson(message));
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    private void broadcast(int gameID, ServerMessage message) {
        String json = gson.toJson(message);
        for (Session s : gameSessions.getOrDefault(gameID, Set.of())) {
            if (s.isOpen()) {
                s.getRemote().sendStringByFuture(json);
            }
        }
    }

    private void broadcastExcept(Session exclude, int gameID, ServerMessage message) {
        String json = gson.toJson(message);
        for (Session s : gameSessions.getOrDefault(gameID, Set.of())) {
            if (!s.equals(exclude) && s.isOpen()) {
                s.getRemote().sendStringByFuture(json);
            }
        }
    }
}

