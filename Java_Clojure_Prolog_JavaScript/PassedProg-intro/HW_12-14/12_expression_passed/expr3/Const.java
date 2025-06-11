package expression;

public class Const implements Expression, LongTripleExpression, TripleExpression {
    private final long con;

    public Const(int con) {
        this.con = con;
    }

    public Const(long con) {
        this.con = con;
    }

    @Override
    public String toString() {
        return Long.toString(con);
    }

    @Override
    public int evaluate(int arg) {
        return (int) con;
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return con;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (int) con;
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Const that) {
            return this.con == that.con;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode((int) con);
    }
}