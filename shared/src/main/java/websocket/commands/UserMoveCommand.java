package websocket.commands;

import chess.ChessMove;


public class UserMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public UserMoveCommand(String authToken, int gameID, ChessMove move){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getmove(){
        return move;
    }
}
