package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Subtract<E> extends BinCalc<E> {
    public Subtract(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType, Sign.SUBTRACT);
    }

    @Override
    protected E calculate(E left, E right) {
        return numberType.sub(left, right);
    }
}