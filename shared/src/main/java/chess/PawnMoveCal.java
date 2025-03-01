package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCal {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        if(color == ChessGame.TeamColor.BLACK){
            add_move(board, myPosition, row-1, col, color, moves);
            diagonal_move(board, myPosition, row-1, col-1, color, moves);
            diagonal_move(board, myPosition, row-1, col+1, color, moves);
        }else{
            add_move(board, myPosition, row+1, col, color, moves);
            diagonal_move(board, myPosition, row+1, col+1, color, moves);
            diagonal_move(board, myPosition, row+1, col-1, color, moves);
        }

        return moves;
    }
    private boolean add_move(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves){
        ChessPosition endPosition = new ChessPosition(row, col);
        int endRow = endPosition.getRow();
        if(!inbounds(endPosition)){ return false;}
        ChessPiece piece = board.getPiece(endPosition);
        if(piece == null){
            if(endRow == 1 || endRow == 8){ //if it can be promoted
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.BISHOP));
                return true;}
            if(start.getRow() == 7 && color == ChessGame.TeamColor.BLACK){ //move twice
                ChessPosition doublePosition = new ChessPosition(row-1, col);
                ChessPiece piece2 = board.getPiece(doublePosition);
                if(piece2 == null) {
                    moves.add(new ChessMove(start, doublePosition, null));
                }
            }
            if(start.getRow() == 2 && color == ChessGame.TeamColor.WHITE){ //move twice
                ChessPosition doublePosition2 = new ChessPosition(row+1, col);
                ChessPiece piece3 = board.getPiece(doublePosition2);
                if(piece3 == null) {
                    moves.add(new ChessMove(start, doublePosition2, null));
                }
            }
            moves.add(new ChessMove(start, endPosition, null));
            return true;
        }else
            return false;
    }


    private  boolean diagonal_move(ChessBoard board, ChessPosition start, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> moves){
        ChessPosition endPosition = new ChessPosition(row, col);
        if(!inbounds(endPosition)) {return false;}
        ChessPiece oppPiece = board.getPiece(endPosition);
        if (oppPiece != null && oppPiece.getTeamColor() != color){
            if(row == 1 || row == 8){ //if it can be promoted
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(start, endPosition, ChessPiece.PieceType.BISHOP));
                return true;}
            else{
                moves.add(new ChessMove(start, endPosition, null));
            }
            return true;
        }
        return false;
    }

    private boolean inbounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
