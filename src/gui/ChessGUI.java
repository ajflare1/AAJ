package gui;
import javax.swing.*;
import java.awt.*;
import board.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pieces.*;


/**
 * Creates the window for the chess game
 */
public class ChessGUI extends JFrame {
    private Board board;
    private JButton[][] squares = new JButton[8][8];
    private String currentTurn = "white";
    private JLabel turnLabel;
    private Piece selectedPiece = null;
    private Position selectedPosition = null;
    private boolean gameOver = false;

    // First line of every saved game file
    private static final String SAVE_HEADER = "CHESS-SAVE-1";

    // Saved board states, one for each move made, used by the Undo button
    private List<Snapshot> undoStack = new ArrayList<>();
    // Details of every move made, shown in the history panel
    private List<MoveRecord> moveHistory = new ArrayList<>();
    private JTextArea historyArea;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private JButton undoButton;
    // Stores the resized piece images so they are only loaded once
    private Map<String, ImageIcon> iconCache = new HashMap<>();

    /**
     * Initializes the chess game window
     */
    public ChessGUI() {
        setTitle("Chess Game");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board = new Board();

        setLayout(new BorderLayout());
        // Displays which player's turn it is
        turnLabel = new JLabel("WHITE'S TURN", JLabel.CENTER);

        // Makes the player turn label larger and bold
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Creates a rounded box for the player turn label
        JPanel turnBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 220, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        turnBox.setOpaque(false);
        turnLabel.setOpaque(false);
        turnBox.add(turnLabel, BorderLayout.CENTER);

        // Sets the size of the rounded turn box
        turnBox.setPreferredSize(new Dimension(220, 45));

        // Centers the rounded box above the chessboard
        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        turnPanel.add(turnBox);
        add(turnPanel, BorderLayout.NORTH);

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
                squares[row][column] = square;

                final int selectedRow = row;
                final int selectedColumn = column;

                // Selects a piece only if it belongs to the player
                square.addActionListener(e -> {
                    if (gameOver) {
                        return;
                    }
                    Piece clickedPiece = board.getPiece(selectedRow, selectedColumn);

                    if (clickedPiece != null && clickedPiece.getColor().equals(currentTurn)) {
                        selectedPiece = clickedPiece;
                        selectedPosition = clickedPiece.getPosition();
                    } else if (selectedPiece != null) { 
                        // Handles the destination square after a piece has been selected 
                        Position destination = new Position(selectedRow, selectedColumn); 

                        // Checks whether the selected piece can move to the destination
                        boolean validMove = false;
                        List<Position> moves = selectedPiece.possibleMoves();

                        for (Position move : moves) {
                            if (move.getRow() == destination.getRow() &&
                                move.getColumn() == destination.getColumn()) {
                                validMove = true;
                                break;
                            }
                        }
                        // Prevents a piece from moving onto a square occupied by a friendly piece
                        if (validMove && clickedPiece != null &&
                            clickedPiece.getColor().equals(currentTurn)) {
                            validMove = false;
                        }
                        // Checks whether the path is clear for pieces that cannot jump
                        if (validMove && (selectedPiece instanceof Rook ||
                                        selectedPiece instanceof Bishop ||
                                        selectedPiece instanceof Queen)) {
                            if (!board.isPathClear(selectedPosition, destination)) {
                                validMove = false;
                            }
                        }
                        // Checks whether a pawn is moving forward or capturing diagonally
                        if (validMove && selectedPiece instanceof Pawn) {
                            int columnDifference = Math.abs(destination.getColumn() - selectedPosition.getColumn());

                            if (columnDifference == 0) {
                                if (clickedPiece != null) {
                                    validMove = false;
                                }
                            } else if (columnDifference == 1) {
                                //Allows a diagonal move to an empty square only for En Passant
                                if (clickedPiece == null && !board.canEnPassant(selectedPosition, destination)) {
                                    validMove = false;
                                }
                            }
                        }
                        // Checks whether a pawn's two-square move is blocked
                        if (validMove && selectedPiece instanceof Pawn &&
                            Math.abs(destination.getRow() - selectedPosition.getRow()) == 2) {
                            int middleRow = (selectedPosition.getRow() + destination.getRow()) / 2;

                            if (board.isOccupied(middleRow, selectedPosition.getColumn())) {
                                validMove = false;
                            }
                        }

                        // Checks whether the King can legally castle
                        if (validMove && selectedPiece instanceof King &&
                            Math.abs(destination.getColumn() - selectedPosition.getColumn()) == 2) {

                            if (!board.canCastle(selectedPosition, destination)) {
                                validMove = false;
                            }
                        }
                        
                        // Prevents moves that leave the player's King in check
                        if (validMove && board.wouldLeaveKingInCheck(selectedPosition, destination)) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Invalid move! Your King would be in CHECK!",
                                "Invalid Move",
                                JOptionPane.WARNING_MESSAGE
                            );
                            validMove = false;
                        }

                        // Moves the selected piece after its destination has been validated
                        if (validMove) {
                            // Saves the game state and move details before the piece moves
                            Piece capturedPiece = capturedPieceFor(board, selectedPosition, destination);
                            saveMove(selectedPiece, selectedPosition, destination, capturedPiece);

                            // Removes the captured Pawn's image when using en passant
                            if (board.canEnPassant(selectedPosition, destination)) {
                                squares[selectedPosition.getRow()][destination.getColumn()].setIcon(null);
                            }

                            board.movePiece(selectedPosition, destination);
                            
                            // Allows the player to choose a piece when promoting a Pawn
                            if (selectedPiece instanceof Pawn &&
                                (destination.getRow() == 0 || destination.getRow() == 7)) {

                                String[] choices = {"Queen", "Rook", "Bishop", "Knight"};
                                String choice = null;

                                while (choice == null) {
                                    choice = (String) JOptionPane.showInputDialog(
                                        this,
                                        "Choose a piece to promote your Pawn to:",
                                        "Pawn Promotion",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        choices,
                                        choices[0]
                                    );
                                }
                                board.promotePawn(destination, choice);

                                // Records the promotion in the move history
                                moveHistory.get(moveHistory.size() - 1).promotion = choice;
                                updateHistoryPanel();
                            }

                                                        
                            // Updates the chess piece images after a valid move
                            squares[destination.getRow()][destination.getColumn()].setIcon(
                            squares[selectedPosition.getRow()][selectedPosition.getColumn()].getIcon());
                            squares[selectedPosition.getRow()][selectedPosition.getColumn()].setIcon(null);

                            // Updates the Rook's image when the King castles
                            if (selectedPiece instanceof King &&
                                Math.abs(destination.getColumn() - selectedPosition.getColumn()) == 2) {

                                int castleRow = selectedPosition.getRow();
                                int rookStartColumn = destination.getColumn() > selectedPosition.getColumn() ? 7 : 0;
                                int rookEndColumn = destination.getColumn() > selectedPosition.getColumn() ? 5 : 3;

                                squares[castleRow][rookEndColumn].setIcon(squares[castleRow][rookStartColumn].getIcon());
                                squares[castleRow][rookStartColumn].setIcon(null);
                            }

                            // Updates the image to match the selected promotion piece
                            Piece promotedPiece = board.getPiece(destination.getRow(), destination.getColumn());

                            if (selectedPiece instanceof Pawn && promotedPiece != selectedPiece) {
                                String color = promotedPiece.getColor();
                                String pieceName = promotedPiece.getClass().getSimpleName().toLowerCase();

                                ImageIcon promotedImage = new ImageIcon("images/" + color + "_" + pieceName + ".png");
                                Image resizedImage = promotedImage.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                                squares[destination.getRow()][destination.getColumn()].setIcon(new ImageIcon(resizedImage));
                            }

                            // Updates the image when a Pawn is promoted to a Queen
                            if (selectedPiece instanceof Pawn && board.getPiece(destination.getRow(), destination.getColumn()) instanceof Queen) {
                                String color = selectedPiece.getColor();
                                ImageIcon queenImage = new ImageIcon("images/" + color + "_queen.png");
                                Image resizedQueen = queenImage.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                                squares[destination.getRow()][destination.getColumn()].setIcon(new ImageIcon(resizedQueen));
                            }

                            // Switches turns after a successful move
                            currentTurn = currentTurn.equals("white") ? "black" : "white";

                            // Updates the turn indicator after each successful move
                            turnLabel.setText(currentTurn.toUpperCase() + "'S TURN");

                       
                            // Ends the game when the opposing King is in checkmate 
                            if (board.isCheckmate(currentTurn)) { 
                                gameOver = true; 

                                String winner = currentTurn.equals("white") ? "BLACK" : "WHITE"; 

                                JOptionPane.showMessageDialog(this, "CHECKMATE! " + winner + " WINS!", "Game Over", 
                                    JOptionPane.INFORMATION_MESSAGE 
                                );                                 
                            } else if (board.isStalemate(currentTurn)) {
                                // Ends the game in a draw when stalemate occurs
                                gameOver = true;

                                JOptionPane.showMessageDialog(
                                    this,
                                    "STALEMATE! The game is a DRAW!",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            } else if (board.isInCheck(currentTurn)) { 
                                JOptionPane.showMessageDialog(
                                    this,
                                    currentTurn.toUpperCase() + " King is in CHECK!",
                                    "Check",
                                    JOptionPane.WARNING_MESSAGE
                                );
                            }

                            selectedPiece = null;
                            selectedPosition = null;
                        }
                    } 
                });

                Piece piece = board.getPiece(row, column);

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
        // Creates a panel to hold the chessboard and its coordinates
        JPanel boardPanel = new JPanel();

        // Adds space around the chessboard and its coordinates
        boardPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Creates the row numbers along the left side
        JPanel rowLabels = new JPanel(new GridLayout(8, 1));

        for (int row = 8; row >= 1; row--) {
            JLabel label = new JLabel("" + row, JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            rowLabels.add(label);
        }

        // Creates the column letters along the bottom
        JPanel columnLabels = new JPanel(new GridLayout(1, 8));

        for (char column = 'A'; column <= 'H'; column++) {
            JLabel label = new JLabel("" + column, JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            columnLabels.add(label);
        }

        // Creates space beneath the row numbers to align the column letters
        JPanel columnPanel = new JPanel(new BorderLayout());
        JPanel corner = new JPanel();
        corner.setPreferredSize(new Dimension(rowLabels.getPreferredSize().width, 0));

        columnPanel.add(corner, BorderLayout.WEST);
        columnPanel.add(columnLabels, BorderLayout.CENTER);

        // Keeps the chessboard square when resizing the window
        boardPanel.setLayout(new LayoutManager() {
            @Override
            public void addLayoutComponent(String name, Component component) {
            }

            @Override
            public void removeLayoutComponent(Component component) {
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return new Dimension(700, 700);
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(200, 200);
            }

            @Override
            public void layoutContainer(Container parent) {
                Insets insets = parent.getInsets();

                int rowWidth = rowLabels.getPreferredSize().width;
                int columnHeight = columnLabels.getPreferredSize().height;

                int availableWidth = parent.getWidth() - insets.left - insets.right;
                int availableHeight = parent.getHeight() - insets.top - insets.bottom;

                int boardSize = Math.max(0, Math.min(
                    availableWidth - rowWidth,
                    availableHeight - columnHeight
                ));

                // Centers the chessboard and its coordinates
                int x = insets.left + (availableWidth - rowWidth - boardSize) / 2;
                int y = insets.top + (availableHeight - columnHeight - boardSize) / 2;

                // Positions the coordinates and chessboard
                rowLabels.setBounds(x, y, rowWidth, boardSize);
                chessboard.setBounds(x + rowWidth, y, boardSize, boardSize);
                columnPanel.setBounds(x, y + boardSize, rowWidth + boardSize, columnHeight);
            }
        });

        // Adds the coordinates and chessboard to the panel
        boardPanel.add(rowLabels);
        boardPanel.add(columnPanel);
        boardPanel.add(chessboard);

        // Adds the chessboard and the coordinates to the center of the window
        add(boardPanel, BorderLayout.CENTER);

        // Adds the move history, captured pieces, and Undo button on the right
        add(buildHistoryPanel(), BorderLayout.EAST);

        // Adds the New Game, Save Game, and Load Game menu
        setJMenuBar(buildMenuBar());
    }

    /**
     * Builds the side panel that shows the move history, the captured pieces
     * for each player, and the Undo button
     * @return The finished history panel
     */
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(300, 0));

        // Scrolling list of moves
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Move History"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Captured pieces for each player
        whiteCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        whiteCapturedPanel.setBorder(BorderFactory.createTitledBorder("Captured by White"));

        blackCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        blackCapturedPanel.setBorder(BorderFactory.createTitledBorder("Captured by Black"));

        JPanel capturedPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        capturedPanel.add(whiteCapturedPanel);
        capturedPanel.add(blackCapturedPanel);
        capturedPanel.setPreferredSize(new Dimension(0, 250));

        // Undo button starts disabled because there is no move to undo yet
        undoButton = new JButton("Undo");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoMove());

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.add(capturedPanel, BorderLayout.CENTER);
        bottomPanel.add(undoButton, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Saves the current board so the move can be undone, adds the move to the
     * history, and updates the history panel. Must be called before the move
     * is made on the board.
     * @param piece The piece that is moving
     * @param start The square the piece is moving from
     * @param end The square the piece is moving to
     * @param captured The piece being captured, or null if no piece is captured
     */
    private void saveMove(Piece piece, Position start, Position end, Piece captured) {
        undoStack.add(new Snapshot(board.copy(), currentTurn));
        moveHistory.add(new MoveRecord(piece, start, end, captured));
        updateHistoryPanel();
    }

    /**
     * Reverts the game to the state before the most recent move, restoring
     * any captured piece, the turn, and the board images
     */
    private void undoMove() {
        if (undoStack.isEmpty()) {
            return;
        }

        Snapshot snapshot = undoStack.remove(undoStack.size() - 1);
        moveHistory.remove(moveHistory.size() - 1);

        board = snapshot.board;
        currentTurn = snapshot.turn;
        gameOver = false;
        selectedPiece = null;
        selectedPosition = null;

        turnLabel.setText(currentTurn.toUpperCase() + "'S TURN");
        refreshBoard();
        updateHistoryPanel();
    }

    /**
     * Redraws every square's image to match the current board
     */
    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = board.getPiece(row, column);
                squares[row][column].setIcon(piece == null ? null : getPieceIcon(piece, 60));
            }
        }
    }

    /**
     * Rebuilds the move list, the captured pieces, and the Undo button state
     * from the move history
     */
    private void updateHistoryPanel() {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < moveHistory.size(); i++) {
            text.append(i + 1).append(". ").append(moveHistory.get(i).describe()).append("\n");
        }

        historyArea.setText(text.toString());
        // Scrolls to the most recent move
        historyArea.setCaretPosition(historyArea.getDocument().getLength());

        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();

        for (MoveRecord record : moveHistory) {
            if (record.captured != null) {
                JLabel label = new JLabel(getPieceIcon(record.captured, 28));

                if (record.piece.getColor().equals("white")) {
                    whiteCapturedPanel.add(label);
                } else {
                    blackCapturedPanel.add(label);
                }
            }
        }

        whiteCapturedPanel.revalidate();
        whiteCapturedPanel.repaint();
        blackCapturedPanel.revalidate();
        blackCapturedPanel.repaint();

        undoButton.setEnabled(!undoStack.isEmpty());
    }

    /**
     * Returns the image for a piece at the given size, loading it only once
     * @param piece The piece to get the image for
     * @param size The width and height of the image in pixels
     * @return The resized piece image
     */
    private ImageIcon getPieceIcon(Piece piece, int size) {
        String name = piece.getColor() + "_" + piece.getClass().getSimpleName().toLowerCase();
        String key = name + size;
        ImageIcon icon = iconCache.get(key);

        if (icon == null) {
            Image image = new ImageIcon("images/" + name + ".png").getImage();
            icon = new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            iconCache.put(key, icon);
        }

        return icon;
    }

    /**
     * Builds the menu bar with the New Game, Save Game, and Load Game items
     * @return The finished menu bar
     */
    private JMenuBar buildMenuBar() {
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newItem = new JMenuItem("New Game");
        newItem.addActionListener(e -> newGame());

        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> saveGame());

        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> loadGame());

        gameMenu.add(newItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(gameMenu);
        return menuBar;
    }

    /**
     * Resets the board and all game data after asking the player to confirm
     * (no confirmation is needed if no moves have been made yet)
     */
    private void newGame() {
        if (!moveHistory.isEmpty()) {
            int answer = JOptionPane.showConfirmDialog(
                this,
                "Start a new game? The current game will be lost.",
                "New Game",
                JOptionPane.YES_NO_OPTION
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        applyGameState(new Board(), "white", new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Asks the player where to save, then saves the current game to that file
     */
    private void saveGame() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setDialogTitle("Save Game");
        chooser.setFileFilter(new FileNameExtensionFilter("Chess save files (*.txt)", "txt"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        // Adds the .txt extension if the player left it off
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }

        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(
                this,
                file.getName() + " already exists. Replace it?",
                "Save Game",
                JOptionPane.YES_NO_OPTION
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            writeSave(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save the game: " + e.getMessage(),
                "Save Game", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Asks the player which file to open, then loads the game saved in it
     */
    private void loadGame() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setDialogTitle("Load Game");
        chooser.setFileFilter(new FileNameExtensionFilter("Chess save files (*.txt)", "txt"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            readSave(chooser.getSelectedFile());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load the game: " + e.getMessage(),
                "Load Game", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Writes the game to a text file. The file holds a header line, then one
     * line per move: start row, start column, end row, end column, and the
     * promotion piece if a Pawn was promoted. Loading replays these moves.
     * @param file The file to write
     * @throws IOException If the file cannot be written
     */
    private void writeSave(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(SAVE_HEADER);

        for (MoveRecord record : moveHistory) {
            String line = record.start.getRow() + "," + record.start.getColumn() + ","
                + record.end.getRow() + "," + record.end.getColumn();

            if (record.promotion != null) {
                line += "," + record.promotion;
            }

            lines.add(line);
        }

        Files.write(file.toPath(), lines);
    }

    /**
     * Loads a game from a file written by writeSave. The moves are replayed
     * on a new board, which restores the pieces, the turn, the move history,
     * the captured pieces, and the Undo history. The current game is only
     * replaced if the whole file is valid.
     * @param file The file to read
     * @throws IOException If the file cannot be read or is not a valid saved game
     */
    private void readSave(File file) throws IOException {
        List<String> lines;

        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new IOException("The file could not be read.");
        }

        if (lines.isEmpty() || !lines.get(0).trim().equals(SAVE_HEADER)) {
            throw new IOException("This is not a saved chess game.");
        }

        Board newBoard = new Board();
        String turn = "white";
        List<Snapshot> newUndoStack = new ArrayList<>();
        List<MoveRecord> newHistory = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            String where = "Line " + (i + 1) + " is not a valid move.";
            String[] parts = line.split(",");

            if (parts.length < 4 || parts.length > 5) {
                throw new IOException(where);
            }

            int[] numbers = new int[4];

            for (int j = 0; j < 4; j++) {
                try {
                    numbers[j] = Integer.parseInt(parts[j].trim());
                } catch (NumberFormatException e) {
                    throw new IOException(where);
                }

                if (numbers[j] < 0 || numbers[j] > 7) {
                    throw new IOException(where);
                }
            }

            Position start = new Position(numbers[0], numbers[1]);
            Position end = new Position(numbers[2], numbers[3]);
            Piece piece = newBoard.getPiece(start.getRow(), start.getColumn());
            Piece target = newBoard.getPiece(end.getRow(), end.getColumn());

            // The moving piece must exist, belong to the player whose turn it is,
            // and not land on one of that player's own pieces
            if (piece == null || !piece.getColor().equals(turn) ||
                (target != null && target.getColor().equals(turn))) {
                throw new IOException(where);
            }

            // A promotion must be listed exactly when a Pawn reaches the last row
            boolean promotes = piece instanceof Pawn && (end.getRow() == 0 || end.getRow() == 7);
            String promotion = parts.length == 5 ? parts[4].trim() : null;

            if (promotes != (promotion != null) ||
                (promotes && !promotion.matches("Queen|Rook|Bishop|Knight"))) {
                throw new IOException(where);
            }

            // Replays the move the same way a click would
            Piece captured = capturedPieceFor(newBoard, start, end);
            newUndoStack.add(new Snapshot(newBoard.copy(), turn));
            MoveRecord record = new MoveRecord(piece, start, end, captured);
            newBoard.movePiece(start, end);

            if (promotes) {
                newBoard.promotePawn(end, promotion);
                record.promotion = promotion;
            }

            newHistory.add(record);
            turn = turn.equals("white") ? "black" : "white";
        }

        applyGameState(newBoard, turn, newUndoStack, newHistory);
    }

    /**
     * Replaces the current game with the given state and redraws the window.
     * Used by New Game and Load Game.
     * @param newBoard The board to play on
     * @param turn The player whose turn it is
     * @param newUndoStack The saved states for the Undo button
     * @param newHistory The moves shown in the history panel
     */
    private void applyGameState(Board newBoard, String turn, List<Snapshot> newUndoStack,
                                List<MoveRecord> newHistory) {
        board = newBoard;
        currentTurn = turn;
        undoStack = newUndoStack;
        moveHistory = newHistory;
        selectedPiece = null;
        selectedPosition = null;

        // A loaded game can already be over (checkmate or stalemate)
        gameOver = board.isCheckmate(currentTurn) || board.isStalemate(currentTurn);

        turnLabel.setText(currentTurn.toUpperCase() + "'S TURN");
        refreshBoard();
        updateHistoryPanel();
    }

    /**
     * Finds the piece a move would capture, including a Pawn taken by en passant
     * @param gameBoard The board the move is made on
     * @param start The square the piece moves from
     * @param end The square the piece moves to
     * @return The captured piece, or null if the move captures nothing
     */
    private Piece capturedPieceFor(Board gameBoard, Position start, Position end) {
        if (gameBoard.canEnPassant(start, end)) {
            return gameBoard.getPiece(start.getRow(), end.getColumn());
        }

        return gameBoard.getPiece(end.getRow(), end.getColumn());
    }

    /**
     * A savedcopy of the board and whose turn it was, used to undo a move
     */
    private static class Snapshot {
        private final Board board;
        private final String turn;

        /**
         * @param board A copy of the board before the move
         * @param turn The player whose turn it was before the move
         */
        Snapshot(Board board, String turn) {
            this.board = board;
            this.turn = turn;
        }
    }

    /**
     * The details of one move, shown in the history panel
     */
    private static class MoveRecord {
        private final Piece piece;
        private final Position start;
        private final Position end;
        private final Piece captured;
        // The piece a Pawn was promoted to (such as "Queen"), or null
        private String promotion = null;

        /**
         * @param piece The piece that moved
         * @param start The square it moved from
         * @param end The square it moved to
         * @param captured The piece it captured, or null
         */
        MoveRecord(Piece piece, Position start, Position end, Piece captured) {
            this.piece = piece;
            this.start = start;
            this.end = end;
            this.captured = captured;
        }

        /**
         * Describes the move, such as "White Pawn: E2 -> E4"
         * @return The text shown in the move history
         */
        String describe() {
            String text = name(piece) + ": " + square(start) + " -> " + square(end);

            if (captured != null) {
                text += " x " + name(captured);
            }

            if (promotion != null) {
                text += " = " + promotion;
            }

            return text;
        }

        // Returns the color and type of a piece, such as "Black Knight"
        private static String name(Piece piece) {
            String color = piece.getColor();
            return Character.toUpperCase(color.charAt(0)) + color.substring(1) + " "
                + piece.getClass().getSimpleName();
        }

        // Returns the board name of a square, such as "E4"
        private static String square(Position position) {
            return "" + (char) ('A' + position.getColumn()) + (8 - position.getRow());
        }
    }
}
