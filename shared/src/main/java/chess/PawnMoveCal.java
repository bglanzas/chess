package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        if (color == ChessGame.TeamColor.BLACK) {
            addMove(board, myPosition, row - 1, col, color, moves);
            diagonalMove(board, myPosition, row - 1, col - 1, color, moves);
            diagonalMove(board, myPosition, row - 1, col + 1, color, moves);
        } else {
            addMove(board, myPosition, row + 1, col, color, moves);
            diagonalMove(board, myPosition, row + 1, col + 1, color, moves);
            diagonalMove(board, myPosition, row + 1, col - 1, color, moves);
        }


        PawnUtils.checkDoubleMove(board, myPosition, color, moves);

        return moves;
    }

    private void addMove(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves) {
        ChessPosition endPosition = new ChessPosition(row, col);
        if (!MoveUtils.inbounds(endPosition)) return;

        if (board.getPiece(endPosition) == null) {
            if (PawnUtils.isPromotionRow(row)) {
                PawnUtils.addPromotions(start, endPosition, moves);
            } else {
                moves.add(new ChessMove(start, endPosition, null));
            }
        }
    }

    private void diagonalMove(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves) {
        ChessPosition endPosition = new ChessPosition(row, col);
        if (!MoveUtils.inbounds(endPosition)) return;

        ChessPiece oppPiece = board.getPiece(endPosition);
        if (oppPiece != null && oppPiece.getTeamColor() != color) {
            if (PawnUtils.isPromotionRow(row)) {
                PawnUtils.addPromotions(start, endPosition, moves);
            } else {
                moves.add(new ChessMove(start, endPosition, null));
            }
        }
    }

}

