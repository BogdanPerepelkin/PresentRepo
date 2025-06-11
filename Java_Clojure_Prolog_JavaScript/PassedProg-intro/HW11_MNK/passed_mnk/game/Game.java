package game;

import java.util.InputMismatchException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Game {
    private final boolean log;
    private final Player player1, player2;

    public Game(final boolean log, final Player player1, final Player player2) {
        this.log = log;
        this.player1 = player1;
        this.player2 = player2;
    }

    public int play(Board board) {
        while (true) {
            final int result1 = move(board, player1, 1);
            if (result1 != -1) {
                return result1;
            }
            final int result2 = move(board, player2, 2);
            if (result2 != -1) {
                return result2;
            }
        }
    }

    private int move(final Board board, final Player player, final int no) {
        try {
            log("Current Field:\n" + board);
            Move move = player.move(board.getPosition(), board.getCell());
            Result result = board.makeMove(move);

            log("Player " + no + " turn: " + move);
            if (result == Result.WIN) {
                log("Player " + no + " won");
                log(board.toString());
                return no;
            } else if (result == Result.LOSE) {
                log("Player " + no + " lose");
                return 3 - no;
            } else if (result == Result.DRAW) {
                log("Draw");
                return 0;
            } else {
                return -1;
            }
        } catch (RuntimeException e) {
            System.out.println("unexpexted runtime exception" + e.getMessage());
            return 3 - no;
        }
    }

    private void log(final String message) {
        if (log) {
            System.out.println(message);
        }
    }
}
