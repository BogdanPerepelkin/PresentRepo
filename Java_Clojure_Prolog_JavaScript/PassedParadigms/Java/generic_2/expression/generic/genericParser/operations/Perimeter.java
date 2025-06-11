package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Perimeter<T> extends BinCalc<T> {
    public Perimeter(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType, Sign.PERIMETER);
    }

    protected T calculate(T left, T right) {
        return numberType.perimeter(left, right);
    }
}