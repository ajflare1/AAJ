package game;
import board.Board;
import java.util.Scanner;
import board.Board;
import gui.ChessGUI;
import javax.swing.SwingUtilities;

/**
 * The class for starting the chess game
 */
public class Game {

    /**
     * Starting the chess game and displaying the board
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launchGUI();
    }
    
    /**
     * Launches the graphical chess game window
     */
    public static void launchGUI() {
        SwingUtilities.invokeLater(() -> { 
            ChessGUI window = new ChessGUI(); 
            window.setVisible(true); 
        }); 
    } 
}
