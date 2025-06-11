package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public abstract class BinCalc<E> implements GenericExpression<E> {
    private final GenericExpression<E> left;
    private final GenericExpression<E> right;
    protected final Sign sign;
    protected final NumberInterface<E> numberType;


    public BinCalc(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType, Sign sign) {
        this.left = left;
        this.right = right;
        this.numberType = numberType;
        this.sign = sign;
    }

    protected abstract E calculate(E left, E right);

    @Override
    public E evaluate(E x, E y, E z) {
        return calculate(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    public String toString() {
        return "(" + left.toString() + " " + sign + " " + right.toString() + ")";
    }
}












