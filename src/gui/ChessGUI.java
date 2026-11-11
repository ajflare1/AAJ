package gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton; 
import java.awt.Color; 

/**
 * Creates the window for the chess game
 */
public class ChessGUI extends JFrame {

    /**
     * Initializes the chess game window
     */
    public ChessGUI() {
        setTitle("Chess Game");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel chessboard = new JPanel();
        chessboard.setLayout(new GridLayout(8, 8));

        // Creating the squares of the chessboard
        for (int row = 0; row < 8; row++) { 
            for (int column = 0; column < 8; column++) { 
                JButton square = new JButton(); 

                // Alternates light and green squares
                if ((row + column) % 2 == 0) { 
                    square.setBackground(new Color(240, 217, 181)); 
                } else { 
                    square.setBackground(new Color(118, 150, 86)); 
                }

                square.setOpaque(true); 
                square.setBorderPainted(false); 

                chessboard.add(square);
            }
        }
        add(chessboard);
    }
}