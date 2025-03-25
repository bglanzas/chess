package ui;

import ui.EscapeSequences;

public class DrawBoard {
    private static final String LightSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String DarkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    public void drawChessboard(boolean isWhitePerspective){
        System.out.print(EscapeSequences.ERASE_SCREEN);
        if(isWhitePerspective){
            drawWhitePerspecitive();
        }else{
            drawBlackPerspective();
        }
    }

    private void drawWhitePerspecitive(){
        for(int row = 8; row >= 1; row--){
            System.out.print(row+" ");
            for(int col = 1; col <= 8; row++){
                drawSquare(row, col);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        drawColumnLabels();
    }

    private void drawBlackPerspective(){
        for(int row = 1; row <= 8; row--){
            System.out.print(row+" ");
            for(int col = 8; col >= 1; row++){
                drawSquare(row, col);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        drawColumnLabels();
    }

    private void drawSquare(int row, int col){
        boolean isLightSquare = (row + col) % 2 ==0;
        String bgColor = isLightSquare ? LightSquare : DarkSquare;
        System.out.print(bgColor + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
    }

    private void drawColumnLabels(){
        System.out.print("  ");
        for(char c = 'a'; c <= 'h'; c++){
            System.out.print(" " + c + " ");
        }
        System.out.println();
    }
}
