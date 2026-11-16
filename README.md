# Java Chess Game

A two-player chess game developed in Java using Swing for the graphical user interface (GUI). The game allows players to move and capture chess pieces while following standard chess rules.

The application includes move validation, turn management, special chess moves, and game-ending conditions such as checkmate and stalemate.

## Features

- **Graphical Chessboard:** An 8×8 chessboard created using Java Swing, with chess piece images and board coordinates.
- **Piece Movement:** Players can select and move pieces according to standard chess rules. Invalid moves are prevented.
- **Piece Capture:** Players can capture opposing pieces through legal moves.
- **Turn Management:** Alternates between White and Black, with an indicator showing whose turn it is.
- **Check Detection:** Displays a warning when a King is in check.
- **Checkmate Detection:** Announces the winner when a player is checkmated.
- **Stalemate Detection:** Detects when a player has no legal moves but is not in check.
- **Pawn Promotion:** Allows Pawns to promote into a Queen, Rook, Bishop, or Knight.
- **En Passant:** Supports the special Pawn capture.
- **Castling:** Supports castling when the required conditions are met.

## How to Compile and Run

**Requirements:**
- Java Development Kit (JDK)
- Visual Studio Code or a terminal

**Instructions:**

1. Open the project in Visual Studio Code.
2. Open a terminal and navigate to the `1stCheckin` folder.
3. Compile the Java files using:

```bash
javac -d out src/board/*.java src/pieces/*.java src/gui/*.java src/game/Game.java
```

4. Run the game using:

```bash
java -cp out game.Game
```

The chess GUI will open, displaying the initial chessboard with all pieces in their starting positions.

## How to Play

1. The game begins with White's turn.
2. Click on a chess piece to select it.
3. Click on the destination square to move the selected piece.
4. If the move is invalid, the piece remains in its original position.
5. Players alternate turns, moving and capturing pieces according to standard chess rules.
6. When a Pawn reaches the opposite end of the board, select a piece from the promotion menu.
7. The game continues until checkmate or stalemate occurs.

When a King is in check, a popup message notifies the players. If checkmate occurs, the game announces the winner.

## Future Features

Additional features and improvements will be added to the chess game in future updates. These may include new gameplay options and enhancements to the graphical user interface.
