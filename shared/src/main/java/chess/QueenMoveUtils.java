package chess;

import java.util.Collection;

public class QueenMoveUtils {
    public static void addMovesRookBishop(ChessBoard board, ChessPosition start,
                                          int rowDir, int colDir, ChessGame.TeamColor color,
                                          Collection<ChessMove> moves) {
        int row = start.getRow();
        int col = start.getColumn();

        while (true) {
            row += rowDir;
            col += colDir;
            if (!MoveUtils.addMove(board, start, row, col, color, moves)) {
                break;
            }
        }
    }
}
