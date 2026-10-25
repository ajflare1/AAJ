package ChessGame.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that checks and converts the text a player types into a {@link Move}.
 * <p>
 * The supported format is {@code [FROM] [TO]}, for example {@code "E2 E4"}. Input is
 * case-insensitive, and extra spaces around or between the squares are ignored.
 */
public final class MoveParser {

    /** Matches two squares (file A-H, rank 1-8) separated by whitespace. */
    private static final Pattern MOVE_PATTERN =
            Pattern.compile("^([A-Ha-h][1-8])\\s+([A-Ha-h][1-8])$");

    /** Matches the castling commands {@code O-O} and {@code O-O-O} (optional feature). */
    private static final Pattern CASTLING_PATTERN =
            Pattern.compile("^O-O(-O)?$", Pattern.CASE_INSENSITIVE);

    /** Utility class; it is not meant to be instantiated. */
    private MoveParser() {
    }

    /**
     * Checks that the input has the form {@code "E2 E4"}. This only checks the format,
     * not whether the move is legal in the current game.
     *
     * @param input the raw text typed by the player (may be null)
     * @return {@code true} if the text is two valid squares separated by a space
     */
    public static boolean isValidFormat(String input) {
        return input != null && MOVE_PATTERN.matcher(input.trim()).matches();
    }

    /**
     * Converts input such as {@code "E2 E4"} into a {@link Move}.
     *
     * @param input the raw text typed by the player (may be null)
     * @return the parsed move, or {@code null} if the format is invalid
     */
    public static Move parse(String input) {
        if (input == null) {
            return null;
        }
        Matcher matcher = MOVE_PATTERN.matcher(input.trim());
        if (!matcher.matches()) {
            return null;
        }
        Position from = Position.fromNotation(matcher.group(1));
        Position to = Position.fromNotation(matcher.group(2));
        return new Move(from, to);
    }

    /**
     * Tells whether the input is a castling command ({@code "O-O"} or {@code "O-O-O"}).
     * Castling is optional, so the game can use this to say it is not supported yet.
     *
     * @param input the raw text typed by the player (may be null)
     * @return {@code true} if the text is a castling command
     */
    public static boolean isCastling(String input) {
        return input != null && CASTLING_PATTERN.matcher(input.trim()).matches();
    }
}