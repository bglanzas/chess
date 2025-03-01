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
        return filterLegalMoves(moves, piece);
    }

    private Collection<ChessMove> filterLegalMoves(Collection<ChessMove> moves, ChessPiece piece) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            if (isLegalMove(move, piece)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private boolean isLegalMove(ChessMove move, ChessPiece piece) {
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

        board.movingPiece(move.getStartPosition(), move.getEndPosition(), piece.getPieceType());
        boolean stillInCheck = isInCheck(piece.getTeamColor());

        board.movingPiece(move.getEndPosition(), move.getStartPosition(), piece.getPieceType());
        board.addPiece(move.getEndPosition(), capturedPiece);

        return !stillInCheck;
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


    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        return isKingUnderAttack(teamColor, kingPosition);
    }

    private boolean isKingUnderAttack(TeamColor teamColor, ChessPosition kingPosition) {
        for (ChessPosition position : getAllOpponentPositions(teamColor)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && canPieceCaptureKing(piece, position, kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private boolean canPieceCaptureKing(ChessPiece piece, ChessPosition piecePosition, ChessPosition kingPosition) {
        for (ChessMove move : piece.pieceMoves(board, piecePosition)) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private Collection<ChessPosition> getAllOpponentPositions(TeamColor teamColor) {
        Collection<ChessPosition> opponentPositions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    opponentPositions.add(position);
                }
            }
        }
        return opponentPositions;
    }



    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyLegalMove(teamColor);
    }

    private boolean hasAnyLegalMove(TeamColor teamColor) {
        for (ChessPosition position : getAllTeamPositions(teamColor)) {
            if (hasLegalMove(position)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLegalMove(ChessPosition position) {
        for (ChessMove move : validMoves(position)) {
            if (isLegalMove(move, board.getPiece(position))) {
                return true;
            }
        }
        return false;
    }

    private Collection<ChessPosition> getAllTeamPositions(TeamColor teamColor) {
        Collection<ChessPosition> teamPositions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamPositions.add(position);
                }
            }
        }
        return teamPositions;
    }


    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyLegalMove(teamColor);
    }


    public void setBoard(ChessBoard board) {
        this.board = board;
    }


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
