package gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

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

        // Loading the chesspiece images
        ImageIcon whiteKing = new ImageIcon("images/white_king.png");
        ImageIcon whiteQueen = new ImageIcon("images/white_queen.png");
        ImageIcon whiteRook = new ImageIcon("images/white_rook.png");
        ImageIcon whiteBishop = new ImageIcon("images/white_bishop.png");
        ImageIcon whiteKnight = new ImageIcon("images/white_knight.png");
        ImageIcon whitePawn = new ImageIcon("images/white_pawn.png");

        ImageIcon blackKing = new ImageIcon("images/black_king.png");
        ImageIcon blackQueen = new ImageIcon("images/black_queen.png");
        ImageIcon blackRook = new ImageIcon("images/black_rook.png");
        ImageIcon blackBishop = new ImageIcon("images/black_bishop.png");
        ImageIcon blackKnight = new ImageIcon("images/black_knight.png");
        ImageIcon blackPawn = new ImageIcon("images/black_pawn.png");

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

                // Displays the white king at E1
                if (row == 7 && column == 4) {
                    Image kingImage = whiteKing.getImage();
                    Image resizedKing = kingImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedKing));
                }

                // Displays the white queen at D1
                if (row == 7 && column == 3) {
                    Image queenImage = whiteQueen.getImage();
                    Image resizedQueen = queenImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedQueen));
                }

                // Displays the white rooks at A1 and H1
                if (row == 7 && (column == 0 || column == 7)) {
                    Image rookImage = whiteRook.getImage();
                    Image resizedRook = rookImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedRook));
                }

                // Displays the white bishops at C1 and F1
                if (row == 7 && (column == 2 || column == 5)) {
                    Image bishopImage = whiteBishop.getImage();
                    Image resizedBishop = bishopImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedBishop));
                }

                // Displays the white knights at B1 and G1
                if (row == 7 && (column == 1 || column == 6)) {
                    Image knightImage = whiteKnight.getImage();
                    Image resizedKnight = knightImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedKnight));
                }

                // Displays the white pawns on the second row
                if (row == 6) {
                    Image pawnImage = whitePawn.getImage();
                    Image resizedPawn = pawnImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedPawn));
                }

                // Displays the black king at E8
                if (row == 0 && column == 4) {
                    Image kingImage = blackKing.getImage();
                    Image resizedKing = kingImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedKing));
                }

                // Displays the black queen at D8
                if (row == 0 && column == 3) {
                    Image queenImage = blackQueen.getImage();
                    Image resizedQueen = queenImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedQueen));
                }

                // Displays the black rooks at A8 and H8
                if (row == 0 && (column == 0 || column == 7)) {
                    Image rookImage = blackRook.getImage();
                    Image resizedRook = rookImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedRook));
                }

                // Displays the black bishops at C8 and F8
                if (row == 0 && (column == 2 || column == 5)) {
                    Image bishopImage = blackBishop.getImage();
                    Image resizedBishop = bishopImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedBishop));
                }

                // Displays the black knights at B8 and G8
                if (row == 0 && (column == 1 || column == 6)) {
                    Image knightImage = blackKnight.getImage();
                    Image resizedKnight = knightImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedKnight));
                }

                // Displays the black pawns on the seventh row
                if (row == 1) {
                    Image pawnImage = blackPawn.getImage();
                    Image resizedPawn = pawnImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    square.setIcon(new ImageIcon(resizedPawn));
                }
            }
        }

        add(chessboard);
    }
}
