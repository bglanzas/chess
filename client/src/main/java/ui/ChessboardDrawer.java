package client;

import ui.EscapeSequences;

public class ChessboardDrawer{

    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    // Unicode Chess Pieces
    private static final String[] WHITE_PIECES = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
    private static final String[] BLACK_PIECES = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};

    private static final String WHITE_PAWN = "♙";
    private static final String BLACK_PAWN = "♟";

    public void drawChessboard(boolean isWhitePerspective) {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        if (isWhitePerspective) {
            drawWhitePerspective();
        } else {
            drawBlackPerspective();
        }
    }

    private void drawWhitePerspective() {
        System.out.println("  a b c d e f g h");
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (int col = 1; col <= 8; col++) {
                drawSquare(row, col);
            }
            System.out.println(" " + row);
        }
        System.out.println("  a b c d e f g h");
    }

    private void drawBlackPerspective() {
        System.out.println("  h g f e d c b a");
        for (int row = 1; row <= 8; row++) {
            System.out.print(row + " ");
            for (int col = 8; col >= 1; col--) {
                drawSquare(row, col);
            }
            System.out.println(" " + row);
        }
        System.out.println("  h g f e d c b a");
    }

    private void drawSquare(int row, int col) {
        boolean isLightSquare = (row + col) % 2 == 0;
        String bgColor = isLightSquare ? LIGHT_SQUARE : DARK_SQUARE;

        // Place Chess Pieces
        if (row == 1) {
            System.out.print(bgColor + WHITE_PIECES[col - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 2) {
            System.out.print(bgColor + WHITE_PAWN + " " + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 7) {
            System.out.print(bgColor + BLACK_PAWN + " " + EscapeSequences.RESET_BG_COLOR);
        } else if (row == 8) {
            System.out.print(bgColor + BLACK_PIECES[col - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        } else {
            System.out.print(bgColor + "  " + EscapeSequences.RESET_BG_COLOR);
        }
    }
}

