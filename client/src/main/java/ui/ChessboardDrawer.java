package client;

import chess.ChessBoard;
import ui.EscapeSequences;

public class ChessboardDrawer {

    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    public void drawChessboard(ChessBoard board, boolean isWhitePerspective) {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        if (isWhitePerspective) {
            drawWhitePerspective();
        } else {
            drawBlackPerspective();
        }
    }

    private void drawWhitePerspective() {
        System.out.println("   a  b  c  d  e  f  g  h");
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (int col = 1; col <= 8; col++) {
                drawSquare(row, col);
            }
            System.out.println(" " + row);
        }
        System.out.println("   a  b  c  d  e  f  g  h");
    }

    private void drawBlackPerspective() {
        System.out.println("   h  g  f  e  d  c  b  a");
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (int col = 8; col >= 1; col--) {
                drawSquare(row, col);
            }
            System.out.println(" " + row);
        }
        System.out.println("   h  g  f  e  d  c  b  a");
    }

    private void drawSquare(int row, int col) {
        boolean isLightSquare = (row + col) % 2 == 0;
        String bgColor = isLightSquare ? DARK_SQUARE : LIGHT_SQUARE;

        if (row == 1) {
            System.out.print(bgColor + getInitialWhitePiece(col) + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 2) {
            System.out.print(bgColor + EscapeSequences.WHITE_PAWN + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 7) {
            System.out.print(bgColor + EscapeSequences.BLACK_PAWN + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 8) {
            System.out.print(bgColor + getInitialBlackPiece(col) + EscapeSequences.RESET_BG_COLOR);
        } else {
            System.out.print(bgColor + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
        }
    }

    private String getInitialWhitePiece(int col) {
        return switch (col) {
            case 1, 8 -> EscapeSequences.WHITE_ROOK;
            case 2, 7 -> EscapeSequences.WHITE_KNIGHT;
            case 3, 6 -> EscapeSequences.WHITE_BISHOP;
            case 4 -> EscapeSequences.WHITE_QUEEN;
            case 5 -> EscapeSequences.WHITE_KING;
            default -> EscapeSequences.EMPTY;
        };
    }

    private String getInitialBlackPiece(int col) {
        return switch (col) {
            case 1, 8 -> EscapeSequences.BLACK_ROOK;
            case 2, 7 -> EscapeSequences.BLACK_KNIGHT;
            case 3, 6 -> EscapeSequences.BLACK_BISHOP;
            case 4 -> EscapeSequences.BLACK_QUEEN;
            case 5 -> EscapeSequences.BLACK_KING;
            default -> EscapeSequences.EMPTY;
        };
    }
}


