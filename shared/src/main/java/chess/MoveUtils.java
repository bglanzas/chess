package chess;

import java.util.Collection;

public class MoveUtils {
    public static boolean addMove(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves) {
        ChessPosition endPosition = new ChessPosition(row, col);
        if (!inbounds(endPosition)) {
            return false;
        }
        ChessPiece piece = board.getPiece(endPosition);
        if (piece == null) {
            moves.add(new ChessMove(start, endPosition, null));
            return true;
        } else if (piece.getTeamColor() != color) {
            moves.add(new ChessMove(start, endPosition, null));
            return false;
        } else {
            return false;
        }
    }

    public static boolean inbounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
