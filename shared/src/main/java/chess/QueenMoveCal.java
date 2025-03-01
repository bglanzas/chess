package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCal {
    public Collection<ChessMove>pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        //up right
        for(int i = row+1, j = col+1; i <= 8 && j <=8; i++, j++){
            if(!MoveUtils.addMove(board, myPosition, i, j, color, moves)) {break;}
        }
        //down left
        for(int i = row-1, j = col-1; i >= 1 && j >=1; i--, j--){
            if(!MoveUtils.addMove(board, myPosition, i, j, color, moves)){ break;}
        }
        //up left
        for(int i = row+1, j = col-1; i <= 8 && j>=1; i++, j--){
            if(!MoveUtils.addMove(board, myPosition, i, j, color, moves)) {break;}
        }
        //down right
        for(int i = row-1, j = col+1; i >= 1 && j<=8; i--, j++){
            if(!MoveUtils.addMove(board, myPosition, i, j, color, moves)) {break;}
        }
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




