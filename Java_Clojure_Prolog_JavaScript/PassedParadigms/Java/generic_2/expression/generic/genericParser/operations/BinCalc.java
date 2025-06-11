package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public abstract class BinCalc<T> implements GenericExpression<T> {
    private final GenericExpression<T> left;
    private final GenericExpression<T> right;
    protected final Sign sign;
    protected final NumberInterface<T> numberType;


    public BinCalc(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType, Sign sign) {
        this.left = left;
        this.right = right;
        this.numberType = numberType;
        this.sign = sign;
    }

    protected abstract T calculate(T left, T right);

    @Override
    public T evaluate(T x, T y, T z) {
        return calculate(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    public String toString() {
        return "(" + left.toString() + " " + sign + " " + right.toString() + ")";
    }
}












