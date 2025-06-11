package expression.generic.genericParser.operations;

import expression.generic.genericParser.*;


public class Area<T> extends BinCalc<T> {
    public Area(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType,Sign.AREA);
    }

    protected T calculate(T left, T right) {
        return numberType.area(left, right);
    }
}