package expression.generic.genericParser.operations;

import expression.generic.genericParser.*;


public class Area<E> extends BinCalc<E> {
    public Area(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType,Sign.AREA);
    }

    protected E calculate(E left, E right) {
        return numberType.area(left, right);
    }
}