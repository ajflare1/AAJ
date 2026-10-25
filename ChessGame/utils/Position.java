package ChessGame.utils;

/**
 * An immutable square on the 8x8 chessboard.
 * <p>
 * A position is stored as a zero-based {@code row} and {@code column} that match the
 * layout of the board's 2D array: row 0 is rank 8 (the top of the console display) and
 * column 0 is file A. Use {@link #fromNotation(String)} and {@link #toNotation()} to
 * convert to and from chess notation such as {@code "E2"}.
 */
public final class Position {

    /** Number of rows and columns on a chessboard. */
    public static final int SIZE = 8;

    /** Zero-based row index (0 = rank 8, 7 = rank 1). */
    private final int row;

    /** Zero-based column index (0 = file A, 7 = file H). */
    private final int column;

    /**
     * Creates a position from board-array coordinates.
     *
     * @param row    zero-based row (0 = rank 8, 7 = rank 1)
     * @param column zero-based column (0 = file A, 7 = file H)
     * @throws IllegalArgumentException if the coordinates are outside the board
     */
    public Position(int row, int column) {
        if (!isValid(row, column)) {
            throw new IllegalArgumentException("Position off the board: " + row + "," + column);
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Tells whether the given coordinates lie on the board.
     *
     * @param row    zero-based row
     * @param column zero-based column
     * @return {@code true} if both values are between 0 and 7 inclusive
     */
    public static boolean isValid(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    /**
     * Parses a square written in chess notation, such as {@code "E2"} or {@code "e2"}.
     *
     * @param notation a file letter A-H followed by a rank digit 1-8
     * @return the matching position
     * @throws IllegalArgumentException if the text is not a valid square
     */
    public static Position fromNotation(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + notation);
        }
        int column = Character.toUpperCase(notation.charAt(0)) - 'A';
        int rank = notation.charAt(1) - '0';
        return new Position(SIZE - rank, column);
    }

    /**
     * Converts this position to chess notation.
     *
     * @return the square name in upper case, such as {@code "E2"}
     */
    public String toNotation() {
        return "" + (char) ('A' + column) + (SIZE - row);
    }

    /**
     * Returns the square a given number of rows and columns away from this one.
     * Pieces use this to walk across the board without worrying about the edges.
     *
     * @param rowOffset    rows to move (negative = toward rank 8)
     * @param columnOffset columns to move (positive = toward file H)
     * @return the new position, or {@code null} if it would fall off the board
     */
    public Position offset(int rowOffset, int columnOffset) {
        int r = row + rowOffset;
        int c = column + columnOffset;
        return isValid(r, c) ? new Position(r, c) : null;
    }

    /**
     * Gets the zero-based row.
     *
     * @return row index (0 = rank 8)
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the zero-based column.
     *
     * @return column index (0 = file A)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Two positions are equal when they refer to the same square.
     *
     * @param o the object to compare with
     * @return {@code true} if {@code o} is a position on the same square
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position other = (Position) o;
        return row == other.row && column == other.column;
    }

    /**
     * Hash code consistent with {@link #equals(Object)}.
     *
     * @return a hash code unique to each of the 64 squares
     */
    @Override
    public int hashCode() {
        return row * SIZE + column;
    }

    /**
     * Returns the square in chess notation.
     *
     * @return the same text as {@link #toNotation()}
     */
    @Override
    public String toString() {
        return toNotation();
    }
}