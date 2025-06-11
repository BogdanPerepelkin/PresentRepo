package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Subtract<T> extends BinCalc<T> {
    public Subtract(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType, Sign.SUBTRACT);
    }

    @Override
    protected T calculate(T left, T right) {
        return numberType.sub(left, right);
    }
}