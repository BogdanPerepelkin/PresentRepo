package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Perimeter<E> extends BinCalc<E> {
    public Perimeter(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType, Sign.PERIMETER);
    }

    protected E calculate(E left, E right) {
        return numberType.perimeter(left, right);
    }
}