package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for Bishop piece
 */
public class Bishop extends Piece {
    
    /**
     * Making A Bishop with a color and starting position
     * @param color The color of the Bishop
     * @param position The position of the Bishop
     */
    public Bishop(String color, Position position) {
        super(color, position);
    }

    /**    
     * Finding the possible diagonal movements in 4 directions of the Bishop piece
     * @return This will return the list of possible positions
     */
    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        //The 4 diagonal movements using a 2D array
        //{-1, -1} means up one and left one, while {1, 1} is down one, right one
        //{n, k} n represents vertical movement, negative number is up/positive is down
        //{n, k} k represents horizontal movement, negative number is left/positive is right
        //Bishop can move multiple times in one direction, so the {n, k} can happen repeatedly 
        int[][] directions = {
            {-1, -1}, {-1, 1},
            {1, -1},  {1, 1}
        };

        //checking the 4 diagonal directions
        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newColumn = column + directions[i][1];

            //ensuring bishops diagonal movement stays on the board
            while (newRow >= 0 && newRow < 8 && newColumn >= 0 && newColumn < 8) {
                moves.add(new Position(newRow, newColumn));

                newRow += directions[i][0];
                newColumn += directions[i][1];
            }
        }
        return moves;
    }
}
