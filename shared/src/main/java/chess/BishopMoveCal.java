package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();


        QueenMoveUtils.addMovesRookBishop(board, myPosition, 1, 1, color, moves);  // Up-right
        QueenMoveUtils.addMovesRookBishop(board, myPosition, -1, -1, color, moves); // Down-left
        QueenMoveUtils.addMovesRookBishop(board, myPosition, 1, -1, color, moves);  // Up-left
        QueenMoveUtils.addMovesRookBishop(board, myPosition, -1, 1, color, moves);  // Down-right

        return moves;
    }
}