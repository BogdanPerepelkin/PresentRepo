package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Multiply<E> extends BinCalc<E> {
    public Multiply(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType, Sign.MULTIPLY);
    }

    @Override
    protected E calculate(E left, E right) {
        return numberType.mult(left, right);
    }
}