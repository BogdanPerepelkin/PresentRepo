package expression.exceptions;

import expression.UnaryMinus;
import expression.parser.GeneralInterface;

public class CheckedNegate extends UnaryMinus {
    public CheckedNegate(GeneralInterface obj) {
        super(obj);
    }

    public int count(int n) {
        if (n == Integer.MIN_VALUE) {
            throw new OverflowIntUnaryException(n, "-");
        }
        return (-1) * n;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return count(obj.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof CheckedNegate that) {
            return this.obj.equals(that.obj);
        }
        return false;
    }
}