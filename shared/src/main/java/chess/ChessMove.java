package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

   
    private final ChessPosition endposition;
    private final ChessPiece.PieceType promotion;
    private final ChessPosition startposition;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startposition = startPosition;
        this.endposition = endPosition;
        this.promotion = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
       return this.startposition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endposition; 
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
            return this.promotion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startposition, chessMove.startposition)
                && Objects.equals(endposition, chessMove.endposition)
                && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startposition, endposition, promotion);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "startposition=" + startposition +
                ", endposition=" + endposition +
                ", promotion=" + promotion +
                '}';
    }
}
