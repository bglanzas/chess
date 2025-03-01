package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurnColor = TeamColor.WHITE;
    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
      ChessPiece piece = board.getPiece(startPosition);
      if (piece == null) {
          return null;
      }
      Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
      Collection<ChessMove> legalMove = new ArrayList<>();
      for(ChessMove move : moves){
          ChessPiece oldPiece = board.getPiece(move.getStartPosition());
          ChessPiece newPiece = board.getPiece(move.getEndPosition());

          board.movingPiece(move.getStartPosition(), move.getEndPosition(), oldPiece.getPieceType());
          if(!isInCheck(piece.getTeamColor())){
              legalMove.add(move);
          }
          board.movingPiece(move.getEndPosition(), move.getStartPosition(), oldPiece.getPieceType());
          board.addPiece(move.getEndPosition(), newPiece);
      }

      return legalMove;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
       ChessPosition startPosition = move.getStartPosition();
       ChessPosition endPosition = move.getEndPosition();
       ChessPiece.PieceType promotion = move.getPromotionPiece();

       ChessPiece piece = board.getPiece(startPosition);
       if (piece == null) {
           throw new InvalidMoveException("There is no piece there");
       }

        Collection<ChessMove> validMoves = validMoves(startPosition);
        if(!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

       if(piece.getTeamColor() != currentTurnColor) {
           throw new InvalidMoveException("Its not your turn!");
       }

        ChessPiece newPiece = board.getPiece(endPosition);
        board.movingPiece(startPosition, endPosition, promotion);

        if(isInCheck(currentTurnColor)) {
            board.addPiece(endPosition, board.getPiece(endPosition));
            board.addPiece(startPosition, newPiece);

            throw new InvalidMoveException("You can't move into Check");
        }

        if(currentTurnColor == TeamColor.WHITE) {
            currentTurnColor = TeamColor.BLACK;
        }else if(currentTurnColor == TeamColor.BLACK) {
            currentTurnColor = TeamColor.WHITE;
        }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> oppMoves = piece.pieceMoves(board, position);
                    for(ChessMove move : oppMoves) {
                        if(move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }

                }

            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return false;
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    for(ChessMove move : moves) {

                        ChessPiece capPiece = board.getPiece(move.getEndPosition());
                        board.movingPiece(position, move.getEndPosition(), piece.getPieceType());
                        boolean stillCheck = isInCheck(teamColor);

                        board.movingPiece(move.getEndPosition(), position, piece.getPieceType());
                        board.addPiece(move.getEndPosition(), capPiece);
                        if(!stillCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    for(ChessMove move : moves) {
                        ChessPiece capPiece = board.getPiece(move.getEndPosition());
                        board.movingPiece(position, move.getEndPosition(), move.getPromotionPiece());

                        boolean noMoves = isInCheck(teamColor);

                        board.movingPiece(move.getEndPosition(), position, piece.getPieceType());
                        board.addPiece(move.getEndPosition(), capPiece);
                        if(!noMoves) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurnColor == chessGame.currentTurnColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurnColor);
    }

}
