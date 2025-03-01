package chess;

import java.util.Collection;

public class PawnUtils {
    public static void addPromotions(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
    }

    public static boolean isPromotionRow(int row) {
        return row == 1 || row == 8;
    }

    public static void checkDoubleMove(ChessBoard board, ChessPosition start, ChessGame.TeamColor color, Collection<ChessMove> moves) {
        int startRow = start.getRow();
        int col = start.getColumn();

        if (color == ChessGame.TeamColor.BLACK && startRow == 7) {
            ChessPosition step1 = new ChessPosition(startRow - 1, col);
            ChessPosition step2 = new ChessPosition(startRow - 2, col);
            if (board.getPiece(step1) == null && board.getPiece(step2) == null) {
                moves.add(new ChessMove(start, step2, null));
            }
        } else if (color == ChessGame.TeamColor.WHITE && startRow == 2) {
            ChessPosition step1 = new ChessPosition(startRow + 1, col);
            ChessPosition step2 = new ChessPosition(startRow + 2, col);
            if (board.getPiece(step1) == null && board.getPiece(step2) == null) {
                moves.add(new ChessMove(start, step2, null));
            }
        }
    }
}


