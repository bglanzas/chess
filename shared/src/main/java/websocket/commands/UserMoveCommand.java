package websocket.commands;

import chess.ChessMove;

public class UserMoveCommand extends UserGameCommand {
    public UserMoveCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID, move);
    }
}
