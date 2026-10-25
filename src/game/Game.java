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

        //Continues to display the board and prompt for entering moves
        while (true) {
            board.display();

            System.out.print("Enter your move (e.g., E2 E4)");
            String move = scanner.nextLine();

            //splitting up the source and destination positions
            //.split(" ") seperates the sting when it enters whitespace
            String[] positions = move.split(" ");

            //Checking to make sure that 2 positions where entered
            //Displays error prompt if less than 2 positions entered
            if (positions.length != 2) {
                System.out.println("Invalid Input. Pleas enter a move like \"E2 E4\"");
                continue;
            }

            String source = positions[0];
            String destination = positions[1];

            //Checking to make sure that both inputs are within the chess board
            if (!source.matches("[A-H][1-8]") || !destination.matches("[A-H][1-8]")) { 
                System.out.println("Invalid coordinates. Use columns A-H and rows 1-8."); 
                continue; 
            } 
        }    
    }
}