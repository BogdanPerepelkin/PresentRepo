package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;


public abstract class UnCalc<E> implements GenericExpression<E> {
    protected final GenericExpression<E> obj;
    protected final NumberInterface<E> numberType;

    public UnCalc(GenericExpression<E> obj, NumberInterface<E> numberType) {
        this.obj = obj;
        this.numberType = numberType;
    }

    public abstract E calculate(E arg);
    public abstract String toString();

    public E evaluate(E x, E y, E z) {
        return calculate(obj.evaluate(x, y, z));
    }
}


