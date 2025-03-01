package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();


        QueenMoveUtils.addMovesRookBishop(board, myPosition, 1, 0, color, moves);  // Up
        QueenMoveUtils.addMovesRookBishop(board, myPosition, -1, 0, color, moves); // Down
        QueenMoveUtils.addMovesRookBishop(board, myPosition, 0, 1, color, moves);  // Right
        QueenMoveUtils.addMovesRookBishop(board, myPosition, 0, -1, color, moves); // Left

        return moves;
    }
}



