package client;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.List;

public class ChessboardDrawer {

    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    public void drawChessboard(ChessBoard board, boolean isWhitePerspective) {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        if (isWhitePerspective) {
            drawBoard(board, 8, 1, 1, 8, null, null);
        } else {
            drawBoard(board, 1, 8, 8, 1, null, null);
        }
    }

    private void drawBoard(ChessBoard board, int rowStart, int rowEnd, int colStart, int colEnd,
                           Collection<ChessPosition> highlights, Collection<ChessPosition> selected) {
        System.out.print("   ");
        for (int col = colStart; col != colEnd + Integer.signum(colEnd - colStart); col += Integer.signum(colEnd - colStart)) {
            System.out.print(" " + (char) ('a' + col - 1) + " ");
        }
        System.out.println();

        for (int row = rowStart; row != rowEnd + Integer.signum(rowEnd - rowStart); row += Integer.signum(rowEnd - rowStart)) {
            System.out.print(row + " ");
            for (int col = colStart; col != colEnd + Integer.signum(colEnd - colStart); col += Integer.signum(colEnd - colStart)) {
                drawSquare(row, col, board, highlights, selected);
            }
            System.out.println(" " + row);
        }

        System.out.print("   ");
        for (int col = colStart; col != colEnd + Integer.signum(colEnd - colStart); col += Integer.signum(colEnd - colStart)) {
            System.out.print(" " + (char) ('a' + col - 1) + " ");
        }
        System.out.println();
    }

    private void drawSquare(int row, int col, ChessBoard board,
                            Collection<ChessPosition> highlights, Collection<ChessPosition> selected) {
        ChessPosition current = new ChessPosition(row, col);
        boolean isLightSquare = (row + col) % 2 == 0;
        String bgColor = isLightSquare ? DARK_SQUARE : LIGHT_SQUARE;

        if (selected != null && selected.contains(current)) {
            bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        } else if (highlights != null && highlights.contains(current)) {
            bgColor = EscapeSequences.SET_BG_COLOR_GREEN;
        }

        var piece = board.getPiece(current);
        String symbol = (piece == null) ? EscapeSequences.EMPTY : getPieceSymbol(piece);
        System.out.print(bgColor + symbol + EscapeSequences.RESET_BG_COLOR);
    }

    public void drawHighlightedBoard(ChessBoard board, boolean isWhitePerspective,
                                     Collection<ChessPosition> highlights, ChessPosition selected) {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        Collection<ChessPosition> selectedList = selected == null ? null : List.of(selected);

        if (isWhitePerspective) {
            drawWhitePerspective(board, highlights, selectedList);
        } else {
            drawBlackPerspective(board, highlights, selectedList);
        }
    }

    private void drawWhitePerspective(ChessBoard board,
                                      Collection<ChessPosition> highlights,
                                      Collection<ChessPosition> selected) {
        System.out.println("   a  b  c  d  e  f  g  h");
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (int col = 1; col <= 8; col++) {
                drawSquare(row, col, board, highlights, selected);
            }
            System.out.println(" " + row);
        }
        System.out.println("   a  b  c  d  e  f  g  h");
    }

    private void drawBlackPerspective(ChessBoard board,
                                      Collection<ChessPosition> highlights,
                                      Collection<ChessPosition> selected) {
        System.out.println("   h  g  f  e  d  c  b  a");
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (int col = 8; col >= 1; col--) {
                drawSquare(row, col, board, highlights, selected);
            }
            System.out.println(" " + row);
        }
        System.out.println("   h  g  f  e  d  c  b  a");
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        };
    }
}




