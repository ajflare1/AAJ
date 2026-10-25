package game;
import board.Board;

/**
 * The class for starting the chess game
 */
public class Game {

    /**
     * Starting the chess game and displaying the board
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        Board board = new Board();
        board.display();
    }
}