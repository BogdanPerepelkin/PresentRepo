package game;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;
    private boolean flag = false;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position board, final Cell cell) {
        out.println(board);
        Move move = new Move(-1, -1, Cell.E);
        boolean userValid = false;
        while (!userValid) {
            try {
                out.println(cell + " turn");
                out.println("Enter row and column separated space characters");
                if (flag) {
                    // заканчиваем с последней в веденной строкой, чтобы не мешала (она может быть с ошибкой)
                    in.nextLine();
                }
                flag = true;
                move = new Move(in.nextInt(), in.nextInt(), cell);
                if (!board.isValid(move)) {
                    System.out.println("Turn " + move + " is invalid");
                    System.out.println("You cant move that, maybe you misclicked, make your turn again");
                    continue;
                }
                userValid = true;
            } catch (InputMismatchException e) {
                System.out.println("Your input is not numbers, fix it. ");
            } catch (NullPointerException e) {
                System.out.println("Your input is null, fix it. ");
            }
        }
        return move;
    }
}
