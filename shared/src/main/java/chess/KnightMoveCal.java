package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessBoard> moves = new ArrayList<ChessBoard>();
        int row = myPosition.getRow();
        int col= myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

















        return moves;
    }

    private boolean add_move(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves){
        ChessPosition endPosition = new ChessPosition(row, col);
        if(!inbounds(endPosition)) return false;
        ChessPiece piece = board.getPiece(endPosition);
        if(piece == null){
            moves.add(new ChessMove(start, endPosition, null));
            return true;
        }else if (piece.getTeamColor() != color){
            moves.add(new ChessMove(start, endPosition, null));
            return false;
        }else
            return false;
    }

    private boolean inbounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 0 && row <= 8 && col >= 0 && col <= 8;
    }

}
