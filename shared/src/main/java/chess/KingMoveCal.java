package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        addMove(board, myPosition, row+1, col, color, moves);
        addMove(board, myPosition, row, col+1, color, moves);
        addMove(board, myPosition, row-1, col, color, moves);
        addMove(board, myPosition, row, col-1, color, moves);
        addMove(board, myPosition, row-1, col-1, color, moves);
        addMove(board, myPosition, row+1, col+1, color, moves);
        addMove(board, myPosition, row-1, col+1, color, moves);
        addMove(board, myPosition, row+1, col-1, color, moves);


        return moves;
    }



    private boolean addMove(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves){
        ChessPosition endPosition = new ChessPosition(row, col);
        if(!inbounds(endPosition)) {return false;}
        ChessPiece piece = board.getPiece(endPosition);
        if(piece == null){
            moves.add(new ChessMove(start, endPosition, null));
            return true;
        }else if (piece.getTeamColor() != color){
            moves.add(new ChessMove(start, endPosition, null));
            return false;
        }else
        {return false;}
    }

    private boolean inbounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
