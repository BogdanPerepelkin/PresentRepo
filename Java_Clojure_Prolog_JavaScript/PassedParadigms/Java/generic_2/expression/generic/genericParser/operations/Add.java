package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.Sign;

public class Add<T> extends BinCalc<T> {
    public Add(GenericExpression<T> left, GenericExpression<T> right, NumberInterface<T> numberType) {
        super(left, right, numberType, Sign.ADD);
    }

    @Override
    protected T calculate(T left, T right) {
        return numberType.add(left, right);
    }
}