package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
            Collection<ChessMove> moves = new ArrayList<>();
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();


            //up
            for(int i = row+1; i <= 8; i++){
                if(!MoveUtils.addMove(board, myPosition, i, col, color, moves)) {break;}
            }
            //down
            for(int i = row-1; i >= 1; i--){
                if(!MoveUtils.addMove(board, myPosition, i, col, color, moves)) {break;}
            }
            //right
            for(int i = col+1; i <= 8; i++){
                if(!MoveUtils.addMove(board, myPosition, row, i, color, moves)) {break;}
            }
            //left
            for(int i = col-1; i >= 1; i--){
                if(!MoveUtils.addMove(board, myPosition, row, i, color, moves)) {break;}
            }
        return moves;
    }

}


