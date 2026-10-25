package board;

/**
 * This will represent a position on the chessboard
 */
public class Position {
    /**
     * row and column position variables
     */
    private final int row;
    private final int column;

    /**
     * 
     * @param row The current row position
     * @param column The current column position
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Getting the position of which row chess piece is in
     * @return This will return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Getting the posotion of which column the chess piece is in
     * @return This will return the column index
     */
    public int getColumn() {
        return column;
    }
}
