package pieces;
import board.Position;
import java.util.List;

/**
 * The Parent class for all chess pieces
 */
public abstract class Piece {
    /**
     * The color and position of the chess piece
     */
    private final String color;
    private Position position;

    /**
     * Creating a chess piece with color and a position
     * @param color The color of the chess piece
     * @param position The position of the chess piece
     */
    public Piece(String color, Position position) {
        this.color = color;
        this.position = position;
    }

    /**
     * Getting the color of the chess piece
     * @return This will return the chess piece color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the current position of the chess piece
     * @return This will return the current position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Updating the chess piece position
     * @param newPosition The new position of chess piece
     */
    public void move(Position newPosition) {
        position = newPosition;
    }

    /**
     * Possible moves for a chess piece
     * @return This will return a list of possible positions
     */
    public abstract List<Position> possibleMoves();

}
