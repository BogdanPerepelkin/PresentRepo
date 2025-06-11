package expression;

import javax.swing.plaf.basic.BasicMenuBarUI;
import java.util.Objects;

public abstract class BinCalc implements TripleExpression, LongTripleExpression, Expression {
    final Expression left;
    final Expression right;
    private final String type;

    public BinCalc(Expression left, Expression right, String type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public Number calculateInt(Number left, Number right) {
        return 0;
    }

    public Number calculateLong(Number left, Number right) {
        return 0;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + type + " " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof BinCalc that) {
            return left.equals(that.left) && right.equals(that.right) && type.equals(that.type);
        }
        return false;
    }

    @Override
    public int evaluate(int arg) {
        return calculateInt(left.evaluate(arg), right.evaluate(arg)).intValue();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calculateInt(((TripleExpression)left).evaluate(x, y, z), ((TripleExpression)right).evaluate(x, y, z)).intValue();
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return calculateLong(((LongTripleExpression)left).evaluateL(x, y, z), ((LongTripleExpression)right).evaluateL(x, y, z)).longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, type);
    }
}











