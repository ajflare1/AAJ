package game;
import board.Board;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.display();

            System.out.print("Enter your move (e.g., E2 E4)");
            String move = scanner.nextLine();

            String[] positions = move.split(" ");

            if (positions.length != 2) {
                System.out.println("Invalid Input. Pleas enter a move like \"E2 E4\"");
                continue;
            }
            
            String source = positions[0];
            String destination = positions[1];
        }    
    }
}