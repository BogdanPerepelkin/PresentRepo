package expression;

import expression.parser.GeneralInterface;

import java.util.Objects;

public abstract class BinCalc extends GeneralInterface {
    private final GeneralInterface left;
    private final GeneralInterface right;
    protected final String type;

    public BinCalc(GeneralInterface left, GeneralInterface right, String type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public abstract Number calculateInt(Number left, Number right);
    public abstract Number calculateLong(Number left, Number right);

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
        return calculateInt(left.evaluate(x, y, z), right.evaluate(x, y, z)).intValue();
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return calculateLong(left.evaluateL(x, y, z), right.evaluateL(x, y, z)).longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, type);
    }
}











