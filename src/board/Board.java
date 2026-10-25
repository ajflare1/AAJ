package board;

import pieces.*;

/**
 * The class for creating and displaying the chessboard
 */
public class Board {

    /**
     * The 8x8 array that stores the chess pieces
     */
    private Piece[][] board;

    /**
     * Creating the chessboard and placing the pieces
     */
    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    /**
     * Placing all chess pieces in their starting positions
     */
    public void initializeBoard() {

        //placing the black pieces
        board[0][0] = new Rook("black", new Position(0, 0));
        board[0][1] = new Knight("black", new Position(0, 1));
        board[0][2] = new Bishop("black", new Position(0, 2));
        board[0][3] = new Queen("black", new Position(0, 3));
        board[0][4] = new King("black", new Position(0, 4));
        board[0][5] = new Bishop("black", new Position(0, 5));
        board[0][6] = new Knight("black", new Position(0, 6));
        board[0][7] = new Rook("black", new Position(0, 7));

        //placing the black pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn("black", new Position(1, i));
        }

        //placing the white pawns
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn("white", new Position(6, i));
        }

        //placing the white pieces
        board[7][0] = new Rook("white", new Position(7, 0));
        board[7][1] = new Knight("white", new Position(7, 1));
        board[7][2] = new Bishop("white", new Position(7, 2));
        board[7][3] = new Queen("white", new Position(7, 3));
        board[7][4] = new King("white", new Position(7, 4));
        board[7][5] = new Bishop("white", new Position(7, 5));
        board[7][6] = new Knight("white", new Position(7, 6));
        board[7][7] = new Rook("white", new Position(7, 7));
    }

    /**
     * Displaying the chessboard with colored squares and text notation
     */
    public void display() {

        //background colors for the chessboard
        String lightSquare = "\u001B[48;5;230m";
        String darkSquare = "\u001B[48;5;107m";
        String resetColor = "\u001B[0m";

        System.out.println("     A    B    C    D    E    F    G    H");

        //displaying each chessboard row using two console lines
        for (int row = 0; row < 8; row++) {

            for (int line = 0; line < 2; line++) {

                //printing the row number on the second line
                if (line == 1) {
                    System.out.print((8 - row) + "  ");
                }
                else {
                    System.out.print("   ");
                }

                for (int column = 0; column < 8; column++) {
                    Piece piece = board[row][column];

                    String squareColor;

                    if ((row + column) % 2 == 0) {
                        squareColor = lightSquare;
                    }
                    else {
                        squareColor = darkSquare;
                    }

                    String symbol = "  ";

                    //displaying the chess pieces on the second line
                    if (line == 1 && piece != null) {
                        String color = piece.getColor().equals("white") ? "w" : "b";

                        if (piece instanceof Pawn) {
                            symbol = color + "p";
                        }
                        else if (piece instanceof Rook) {
                            symbol = color + "R";
                        }
                        else if (piece instanceof Knight) {
                            symbol = color + "N";
                        }
                        else if (piece instanceof Bishop) {
                            symbol = color + "B";
                        }
                        else if (piece instanceof Queen) {
                            symbol = color + "Q";
                        }
                        else if (piece instanceof King) {
                            symbol = color + "K";
                        }
                    }

                    System.out.print(squareColor + "  " + symbol + " " + resetColor);
                }

                System.out.println();
            }
        }
    }
}