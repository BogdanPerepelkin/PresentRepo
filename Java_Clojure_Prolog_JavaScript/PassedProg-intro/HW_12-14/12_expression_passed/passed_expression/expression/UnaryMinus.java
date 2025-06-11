package expression;

import java.util.Objects;

public class UnaryMinus implements TripleExpression, Expression, LongTripleExpression {
    private final TripleExpression obj;

    public UnaryMinus(TripleExpression obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "-" + obj.toString();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (-1) * obj.evaluate(x, y, z);
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof UnaryMinus that) {
            return this.obj.equals(that.obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }

    @Override
    public int evaluate(int x) {
        return 0;
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return 0;
    }
}