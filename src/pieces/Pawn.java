package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for Pawn pieces
 */
public class Pawn extends Piece {

    /**
     * Makes a pawn with a color and starting position
     * @param color The specific color of the pawn
     * @param position The position of the pawn
     */
    public Pawn(String color, Position position) {
        super(color, position);
    }

    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        int direction = getColor().equals("White") ? -1 : 1;
        int startingRow = getColor().equals("White") ? 6 : 1;

        int nextRow = row + direction;

        if (nextRow >= 0 && nextRow < 8) {
            moves.add(new Position(nextRow, column));
        }

        if (row == startingRow) {
            moves.add(new Position(row + (2 * direction), column));
        }

        return moves;
    } 

}