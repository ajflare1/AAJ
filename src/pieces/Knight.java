package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for Knight pieces
 */
public class Knight extends Piece {

    /**
     * Making a Knight with a color and starting position
     * @param color The color of the knight
     * @param position The position of the knight
     */
    public Knight(String color, Position position) {
        super(color, position);
    }

    /**  
     * Finding the L-Shaped movement of Knight piece
     * @return This will return the list of possible positions
     */
    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        //The 8 possible moves for Knight piece using 2D array
        //{-2, -1} means up two and left 1, while {1, 2} is down one, right two
        //{n, k} n represents vertical movement, negative number is up/positive is down
        //{n, k} k represents horizontal movement, negative number is left/positive is right
        int[][] directions = {
            {-2, -1}, {-2, 1},
            {-1, -2}, {-1,2},
            {1, -2},  {1, 2},
            {2, -1},  {2, 1}
        };

        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newColumn = column + directions[i][1];

            //condition to ensure that the move remains on the 8x8 chessboard
            if (newRow >= 0 && newRow < 8 && newColumn >= 0 && newColumn < 8) {
                moves.add(new Position(newRow, newColumn));
            }
        }
        return moves;
    }
}
