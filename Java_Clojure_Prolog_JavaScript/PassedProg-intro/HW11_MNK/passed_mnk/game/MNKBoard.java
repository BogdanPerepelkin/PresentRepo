package game;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class MNKBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;
    private final int m;
    private final int n;
    private final int k;
    private final int p;
    private int countFill;
    private int countTurn = 1;

    public MNKBoard(int m, int n, int k, int p) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.p = p;
        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    public Position getPosition() {
        MNKBoard thisObj = this;
        return new Position() {
            @Override
            public boolean isValid(Move move) {
                return thisObj.isValid(move);
            }

            @Override
            public Cell getCell(int r, int c) {
                return cells[r][c];
            }

            @Override
            public String toString() {
                return thisObj.toString();
            }
        };
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    public boolean checkSide(int row, int col, int horizonSide, int verticalSide) {
        int count = 0;
        for (int i = 0; i < k * 2; i++) {
            int curCol = col + i * horizonSide;
            int curRow = row + i * verticalSide;
            if (curRow >= 0 && curRow < m
                    && curCol >= 0 && curCol < n
                    && cells[curRow][curCol] == turn) {
                count++;
                if (count == k) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        // пробегаемся оптимизированной снежинкой
        countFill++;
        int curRow = move.getRow();
        int curColumn = move.getColumn();
        cells[curRow][curColumn] = move.getValue();

        if (checkSide(curRow, curColumn - k + 1, 1, 0)
                || checkSide(curRow - k + 1, curColumn, 0, 1)
                || checkSide(curRow - k + 1, curColumn - k + 1, 1, 1)
                || checkSide(curRow + k - 1, curColumn - k + 1, 1, -1)) {
            return Result.WIN;
        }

        if (countFill == m * n) {
            return Result.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        countTurn++;
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        boolean regularCheck = 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();

        boolean debutRegulations = true;
        if (countTurn <= p) {
            int length = -m % 2 + 2 * countTurn;
            int width = -n % 2 + 2 * countTurn;
            int centerM = m / 2;
            int centerN = n / 2;
            debutRegulations = move.getRow() >= centerM - length && move.getRow() <= centerM + length
                    && move.getColumn() >= centerN - width && move.getColumn() <= centerN + width;
        }

        return regularCheck && debutRegulations;
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int maxLenCol = Integer.toString(n).length();
        int maxLenRow = Integer.toString(m).length();
        sb.append(" ".repeat(maxLenRow));
        for (int i = 0; i < n; i++) {
            sb.append(i).append(" ".repeat(maxLenCol + 1 - Integer.toString(i).length()));
        }
        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r);
            int curL = Integer.toString(r).length();
            sb.append(" ".repeat(Math.max(0, maxLenRow - curL)));
            for (int c = 0; c < n; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
                sb.append(" ".repeat(maxLenCol));
            }
        }
        return sb.toString();
    }
}

