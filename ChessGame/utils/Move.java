package ChessGame.utils;

/**
 * An immutable chess move: the square a piece starts on and the square it moves to.
 * <p>
 * A move only describes what the player asked for. It does not know whether the move is
 * legal; the {@code Board} and {@code Piece} classes decide that.
 */
public final class Move {

    /** The square the piece starts on. */
    private final Position from;

    /** The square the piece is moving to. */
    private final Position to;

    /**
     * Creates a move.
     *
     * @param from the starting square (must not be null)
     * @param to   the destination square (must not be null)
     * @throws IllegalArgumentException if either square is null
     */
    public Move(Position from, Position to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("A move needs both a start and an end square.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the starting square.
     *
     * @return the position the piece moves from
     */
    public Position getFrom() {
        return from;
    }

    /**
     * Gets the destination square.
     *
     * @return the position the piece moves to
     */
    public Position getTo() {
        return to;
    }

    /**
     * Two moves are equal when they have the same start and end squares.
     *
     * @param o the object to compare with
     * @return {@code true} if {@code o} is a move with the same squares
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        Move other = (Move) o;
        return from.equals(other.from) && to.equals(other.to);
    }

    /**
     * Hash code consistent with {@link #equals(Object)}.
     *
     * @return a hash code built from both squares
     */
    @Override
    public int hashCode() {
        return 31 * from.hashCode() + to.hashCode();
    }

    /**
     * Describes the move in the same format the player types, e.g. {@code "E2 E4"}.
     *
     * @return the start square, a space, then the end square
     */
    @Override
    public String toString() {
        return from + " " + to;
    }
}