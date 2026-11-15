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
     * Returns the chess piece at the given row and column
     */
    public Piece getPiece(int row, int column) { 
        return board[row][column]; 
    }

    /**
     * Finds the position of the King belonging to the given color
     */
    public Position findKing(String color) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = board[row][column];

                if (piece instanceof King && piece.getColor().equals(color)) {
                    return piece.getPosition();
                }
            }
        }

        return null;
    }

    /**
     * Checks whether the King of the given color is in check
     */
    public boolean isInCheck(String color) {
        Position kingPosition = findKing(color);

        if (kingPosition == null) {
            return false;
        }

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = board[row][column];

                if (piece != null && !piece.getColor().equals(color)) {

                    if (piece instanceof Pawn) {
                        int direction = piece.getColor().equals("white") ? -1 : 1;

                        if (kingPosition.getRow() == row + direction &&
                            Math.abs(kingPosition.getColumn() - column) == 1) {
                            return true;
                        }
                    } else {
                        for (Position move : piece.possibleMoves()) {
                            if (move.getRow() == kingPosition.getRow() &&
                                move.getColumn() == kingPosition.getColumn()) {

                                if (piece instanceof Rook ||
                                    piece instanceof Bishop ||
                                    piece instanceof Queen) {

                                    if (isPathClear(piece.getPosition(), kingPosition)) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether a move would leave the player's King in check
     */
    public boolean wouldLeaveKingInCheck(Position start, Position end) {
        Piece piece = board[start.getRow()][start.getColumn()];
        Piece capturedPiece = board[end.getRow()][end.getColumn()];

        // Temporarily moves the piece
        board[end.getRow()][end.getColumn()] = piece;
        board[start.getRow()][start.getColumn()] = null;
        piece.move(end);

        boolean kingInCheck = isInCheck(piece.getColor());

        // Restores the pieces to their original positions
        board[start.getRow()][start.getColumn()] = piece;
        board[end.getRow()][end.getColumn()] = capturedPiece;
        piece.move(start);

        return kingInCheck;
    }

    /**
     * Checks whether the King can castle in the chosen direction
     */
    public boolean canCastle(Position start, Position end) {
        Piece king = board[start.getRow()][start.getColumn()];

        if (!(king instanceof King) || king.hasMoved()) {
            return false;
        }

        int row = start.getRow();
        int direction = end.getColumn() > start.getColumn() ? 1 : -1;
        int rookColumn = direction == 1 ? 7 : 0;

        Piece rook = board[row][rookColumn];

        // Checks whether the Rook is eligible for castling
        if (!(rook instanceof Rook) ||
            !rook.getColor().equals(king.getColor()) ||
            rook.hasMoved()) {
            return false;
        }

        // Checks whether any pieces are between the King and Rook
        if (!isPathClear(start, new Position(row, rookColumn))) {
            return false;
        }

        // Prevents castling while the King is in check
        if (isInCheck(king.getColor())) {
            return false;
        }

        // Checks whether the King passes through an attacked square
        Position middle = new Position(row, start.getColumn() + direction);

        if (wouldLeaveKingInCheck(start, middle)) {
            return false;
        }

        // Checks whether the King's destination is safe
        if (wouldLeaveKingInCheck(start, end)) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a chess piece can legally move to a destination
     */
    public boolean isLegalMove(Position start, Position end) {
        Piece piece = board[start.getRow()][start.getColumn()];
        Piece destinationPiece = board[end.getRow()][end.getColumn()];

        if (piece == null) {
            return false;
        }

        // Checks whether the destination is a possible move
        boolean validMove = false;

        for (Position move : piece.possibleMoves()) {
            if (move.getRow() == end.getRow() &&
                move.getColumn() == end.getColumn()) {
                validMove = true;
                break;
            }
        }

        if (!validMove) {
            return false;
        }

        // Checks whether a King's two-square move is a legal castle
        if (piece instanceof King &&
            Math.abs(end.getColumn() - start.getColumn()) == 2) {

            if (!canCastle(start, end)) {
                return false;
            }
        }

        // Prevents capturing a friendly piece or the opposing King
        if (destinationPiece != null) {
            if (destinationPiece.getColor().equals(piece.getColor()) ||
                destinationPiece instanceof King) {
                return false;
            }
        }

        // Checks whether the path is clear
        if (piece instanceof Rook ||
            piece instanceof Bishop ||
            piece instanceof Queen) {

            if (!isPathClear(start, end)) {
                return false;
            }
        }

        // Checks the Pawn's movement and capturing rules
        if (piece instanceof Pawn) {
            int columnDifference = Math.abs(end.getColumn() - start.getColumn());

            if (columnDifference == 0) {
                if (destinationPiece != null) {
                    return false;
                }
            } else if (columnDifference == 1) {
                if (destinationPiece == null) {
                    return false;
                }
            }

            if (Math.abs(end.getRow() - start.getRow()) == 2) {
                int middleRow = (start.getRow() + end.getRow()) / 2;

                if (isOccupied(middleRow, start.getColumn())) {
                    return false;
                }
            }
        }

        // Prevents moves that leave the player's King in check
        if (wouldLeaveKingInCheck(start, end)) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the King of the given color is in checkmate
     */
    public boolean isCheckmate(String color) {
        if (!isInCheck(color)) {
            return false;
        }

        // Checks whether any piece can make a legal move
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = board[row][column];

                if (piece != null && piece.getColor().equals(color)) {
                    Position start = piece.getPosition();

                    for (Position end : piece.possibleMoves()) {
                        if (isLegalMove(start, end)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Checks whether the King of the given color is in stalemate
     */
    public boolean isStalemate(String color) {
        if (isInCheck(color)) {
            return false;
        }

        // Checks whether any piece can make a legal move
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = board[row][column];

                if (piece != null && piece.getColor().equals(color)) {
                    Position start = piece.getPosition();

                    for (Position end : piece.possibleMoves()) {
                        if (isLegalMove(start, end)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    /**
     * Moves a chess piece from its starting position to its destination
     */
    public void movePiece(Position start, Position end) {
        Piece piece = board[start.getRow()][start.getColumn()];

        board[end.getRow()][end.getColumn()] = piece;
        board[start.getRow()][start.getColumn()] = null;

        piece.move(end);
        piece.setHasMoved(true);

        // Moves the Rook automatically when the King castles
        if (piece instanceof King &&
            Math.abs(end.getColumn() - start.getColumn()) == 2) {

            int row = start.getRow();
            int rookStartColumn = end.getColumn() > start.getColumn() ? 7 : 0;
            int rookEndColumn = end.getColumn() > start.getColumn() ? 5 : 3;

            Piece rook = board[row][rookStartColumn];

            board[row][rookEndColumn] = rook;
            board[row][rookStartColumn] = null;

            rook.move(new Position(row, rookEndColumn));
            rook.setHasMoved(true);
        }
    }

    /**
     * Promotes a pawn to the selected piece when it reaches the opposite end of the board
     */
    public void promotePawn(Position position, String choice) {
        Piece piece = board[position.getRow()][position.getColumn()];

        if (piece instanceof Pawn) {
            if ((piece.getColor().equals("white") && position.getRow() == 0) ||
                (piece.getColor().equals("black") && position.getRow() == 7)) {

                if (choice.equals("Queen")) {
                    board[position.getRow()][position.getColumn()] = new Queen(piece.getColor(), position);
                } else if (choice.equals("Rook")) {
                    board[position.getRow()][position.getColumn()] = new Rook(piece.getColor(), position);
                } else if (choice.equals("Bishop")) {
                    board[position.getRow()][position.getColumn()] = new Bishop(piece.getColor(), position);
                } else if (choice.equals("Knight")) {
                    board[position.getRow()][position.getColumn()] = new Knight(piece.getColor(), position);
                }
            }
        }
    }

    /**
     * Checks whether a square contains a chess piece
     */
    public boolean isOccupied(int row, int column) {
        return board[row][column] != null;
    }

    /**
     * Checks whether any pieces are blocking the path between two positions
     */
    public boolean isPathClear(Position start, Position end) {
        int rowDifference = end.getRow() - start.getRow();
        int columnDifference = end.getColumn() - start.getColumn();

        int rowStep = Integer.compare(rowDifference, 0);
        int columnStep = Integer.compare(columnDifference, 0);

        int row = start.getRow() + rowStep;
        int column = start.getColumn() + columnStep;

        // Checks each square between the starting and ending positions
        while (row != end.getRow() || column != end.getColumn()) {
            if (isOccupied(row, column)) {
                return false;
            }

            row += rowStep;
            column += columnStep;
        }

        return true;
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