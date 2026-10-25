package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for Queen piece
 */
public class Queen extends Piece {
    
    /**
     * Making a Queen with a color and starting position
     * @param color The color of the Queen
     * @param position The position of the Queen
     */
    public Queen(String color, Position position) {
        super(color, position);
    }

    /**   
     * This will find all possible movements in 8 directions
     * @return This will return the list of possible postion
     */
    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        //The 8 possible movements of the Queen
        //Movement combines Rook and Bishop movement logic
        //{n, k} n represents vertical movement, negative number is up/positive is down
        //{n, k} k represents horizontal movement, negative number is left/positive is right
        //Queen can move multiple times in one direction
        int[][] directions = {
            {-1, 0}, {1, 0},
            {0, -1}, {0, 1}, 
            {-1, -1}, {-1, 1},
            {1, -1}, {1, 1}
        };

        //checking all 8 directions
        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newColumn = column + directions[i][1];

            //ensuring movement stays on the board
            while (newRow >= 0 && newRow < 8 && newColumn >= 0 && newColumn < 8) {
                moves.add(new Position(newRow, newColumn));

                newRow += directions[i][0];
                newColumn += directions[i][1];
            }
        }
        return moves;
    }
}
