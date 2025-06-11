package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Add<E> extends BinCalc<E> {
    public Add(GenericExpression<E> left, GenericExpression<E> right, NumberInterface<E> numberType) {
        super(left, right, numberType, Sign.ADD);
    }

    @Override
    protected E calculate(E left, E right) {
        return numberType.add(left, right);
    }
}