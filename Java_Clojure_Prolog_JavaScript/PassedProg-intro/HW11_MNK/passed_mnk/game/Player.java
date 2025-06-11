package game;

import java.util.InputMismatchException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Player {
    Move move(Position position, Cell cell);
}
