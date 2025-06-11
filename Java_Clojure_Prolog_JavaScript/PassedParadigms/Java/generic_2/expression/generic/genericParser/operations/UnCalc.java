package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;


public abstract class UnCalc<T> implements GenericExpression<T> {
    protected final GenericExpression<T> obj;
    protected final NumberInterface<T> numberType;

    public UnCalc(GenericExpression<T> obj, NumberInterface<T> numberType) {
        this.obj = obj;
        this.numberType = numberType;
    }

    public abstract T calculate(T arg);
    public abstract String toString();

    public T evaluate(T x, T y, T z) {
        return calculate(obj.evaluate(x, y, z));
    }
}


