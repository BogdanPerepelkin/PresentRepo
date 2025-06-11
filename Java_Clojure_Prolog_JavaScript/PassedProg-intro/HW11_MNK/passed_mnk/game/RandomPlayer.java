package game;

import java.util.Random;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class RandomPlayer implements Player {
    private final Random random;
    private final int m;
    private final int n;

    public RandomPlayer(int m, int n, Random random) {
        this.m = m;
        this.n = n;
        this.random = random;
    }

    @Override
    public Move move(Position position, final Cell cell) {
        int r = random.nextInt(m);
        int c = random.nextInt(n);
        return new Move(r, c, cell);
    }
}
