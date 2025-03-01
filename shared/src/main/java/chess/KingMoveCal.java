package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        MoveUtils.addMove(board, myPosition, row+1, col, color, moves);
        MoveUtils.addMove(board, myPosition, row, col+1, color, moves);
        MoveUtils.addMove(board, myPosition, row-1, col, color, moves);
        MoveUtils.addMove(board, myPosition, row, col-1, color, moves);
        MoveUtils.addMove(board, myPosition, row-1, col-1, color, moves);
        MoveUtils.addMove(board, myPosition, row+1, col+1, color, moves);
        MoveUtils.addMove(board, myPosition, row-1, col+1, color, moves);
        MoveUtils.addMove(board, myPosition, row+1, col-1, color, moves);


        return moves;
    }

}
