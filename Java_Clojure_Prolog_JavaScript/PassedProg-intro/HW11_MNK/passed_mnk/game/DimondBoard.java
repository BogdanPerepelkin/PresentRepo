package game;

import java.util.Map;

public class DimondBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.',
            Cell.OOB, '#'
    );

    private final Cell[][] cells;
    private Cell turn;
    private final int m;
    private final int k;
    private final int p;
    private int countFill = 0;
    private int countTurn = 1;

    public DimondBoard(int m, int k, int p) {
        this.m = 2 * m - 1;
        this.k = k;
        this.p = p;

        // заполнение ромбового поля
        this.cells = new Cell[this.m][this.m];
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.m; j++) {
                if (j < Math.abs(m - i - 1) || j > 2 * m - 2 - Math.abs(m - 1 - i)) {
                    cells[i][j] = Cell.OOB;
                    countFill++;
                } else {
                    cells[i][j] = Cell.E;
                }
            }
        }
        turn = Cell.X;
    }

    @Override
    public Position getPosition() {
        DimondBoard thisObj = this;
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
                    && curCol >= 0 && curCol < m
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
        countFill++;

        // пробегаемся оптимизированной снежинкой
        int curRow = move.getRow();
        int curColumn = move.getColumn();
        cells[curRow][curColumn] = move.getValue();

        if (checkSide(curRow, curColumn - k + 1, 1, 0)
                || checkSide(curRow - k + 1, curColumn, 0, 1)
                || checkSide(curRow - k + 1, curColumn - k + 1, 1, 1)
                || checkSide(curRow + k - 1, curColumn - k + 1, 1, -1)) {
            return Result.WIN;
        }

        if (countFill == m * m) {
            return Result.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        countTurn++;
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        boolean regularCheck = 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < m
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();

        boolean debutRegulations;
        if (countTurn <= p) {
            int length = -m % 2 + 2 * countTurn;
            int center = m / 2;
            debutRegulations = move.getRow() >= center - length && move.getRow() <= center + length
                    && move.getColumn() >= center - length && move.getColumn() <= center + length;
        } else {
            debutRegulations = true;
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
        int maxLenCol = Integer.toString(m).length();
        int maxLenRow = Integer.toString(m).length();
        sb.append(" ".repeat(maxLenRow));
        for (int i = 0; i < m; i++) {
            sb.append(i).append(" ".repeat(maxLenCol + 1 - Integer.toString(i).length()));
        }
        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r);
            int curL = Integer.toString(r).length();
            sb.append(" ".repeat(Math.max(0, maxLenRow - curL)));
            for (int c = 0; c < m; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
                sb.append(" ".repeat(maxLenCol));
            }
        }
        return sb.toString();
    }
}

