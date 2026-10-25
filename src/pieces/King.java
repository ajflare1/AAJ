package pieces;
import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for King piece
 */
public class King extends Piece {
    
    /**
     * Making a King with a color and starting position
     * @param color The color of the King
     * @param position The position of the King
     */
    public King(String color, Position position) {
        super(color, position);
    }

    /**   
     * This will find all possible movements in 8 directions
     * @return This will return the list of possible postions
     */
    @Override 
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();

        int row = getPosition().getRow();
        int column = getPosition().getColumn();

        //The 8 possible movements of the King
        //Movement combines Rook and Bishop movement logic
        //{n, k} n represents vertical movement, negative number is up/positive is down
        //{n, k} k represents horizontal movement, negative number is left/positive is right
        //King can only move one time in single a direction
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
            if (newRow >= 0 && newRow < 8 && newColumn >= 0 && newColumn < 8) {
                moves.add(new Position(newRow, newColumn));
            }
        }
        return moves;
    }
}
