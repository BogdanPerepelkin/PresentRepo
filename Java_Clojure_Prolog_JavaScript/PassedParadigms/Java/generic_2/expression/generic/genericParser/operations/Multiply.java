package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Multiply<T> extends BinCalc<T> {
    public Multiply(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType, Sign.MULTIPLY);
    }

    @Override
    protected T calculate(T left, T right) {
        return numberType.mult(left, right);
    }
}