package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for Rook pieces
 */
public class Rook extends Piece {
    /**
     * Making a rook with a color and starting position
     * @param color The color of the rook
     * @param position The position of the rook
     */
    public Rook(String color, Position position) {
        super(color, position);
    }

    /**   
     * Find the horizontal and vertical movement of Rook piece
     * @return This will return the list of possible positions
     */
    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        //Checking all the rows in the column its in
        for (int i = 0; i < 8; i++) {
            if (i != row) {
                moves.add(new Position(i, column));
            }
        }

        //checking all the columns in the row its in
        for (int i = 0; i < 8; i++) {
            if (i != column) {
                moves.add(new Position(row, i));
            }
        }
        return moves;
    }
}
