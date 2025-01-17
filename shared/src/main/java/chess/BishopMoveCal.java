package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();


        //up right
        for(int i = row+1, j = col+1; i <= 8 && j <=8; i++, j++){
            if(piece_move(board, myPosition, i, j, color, moves)) break;
        }
        //down left
        for(int i = row-1, j = col-1; i >= 1 && j >=1; i--, j--){
            if(piece_move(board, myPosition, i, j, color, moves)) break;
        }
        //up left
        for(int i = row+1, j = col-1; i <= 8 && j>=1; i++, j--){
            if(piece_move(board, myPosition, i, j, color, moves)) break;
        }
        //down right
        for(int i = row-1, j = col+1; i >= 1 && j<=8; i--, j++){
            if(piece_move(board, myPosition, i, j, color, moves)) break;
        }
        return moves;
    }

    private boolean piece_move(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves){
        ChessPosition endPosition = new ChessPosition(row, col);
        if(!inbounds(endPosition)) return true;
        ChessPiece piece = board.getPiece(endPosition);
        if(piece == null){
            moves.add(new ChessMove(start, endPosition, null));
            return false;
        }else if (piece.getTeamColor() != color){
            moves.add(new ChessMove(start, endPosition, null));
            return true;
        }else
            return true;
    }

    private boolean inbounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 0 && row <= 8 && col >= 0 && col <= 8;
    }

}
