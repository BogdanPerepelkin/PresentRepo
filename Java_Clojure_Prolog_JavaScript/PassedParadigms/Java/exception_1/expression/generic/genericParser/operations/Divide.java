package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Divide<E> extends BinCalc<E> {
    public Divide(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType, Sign.DIVIDE);
    }

    @Override
    protected E calculate(E left, E right) {
        return numberType.divide(left, right);
    }
}