package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Divide<T> extends BinCalc<T> {
    public Divide(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType, Sign.DIVIDE);
    }

    @Override
    protected T calculate(T left, T right) {
        return numberType.divide(left, right);
    }
}