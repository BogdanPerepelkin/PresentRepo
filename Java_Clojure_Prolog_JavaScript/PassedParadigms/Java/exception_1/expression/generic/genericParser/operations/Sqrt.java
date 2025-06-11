package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;

public class Sqrt<E> extends UnCalc<E> {
    public Sqrt(GenericExpression<E> obj, NumberInterface<E> numberType) {
        super(obj, numberType);
    }

    public String toString() {
        return "√(" + obj.toString() + ")";
    }

    @Override
    public E calculate(E arg) {
        return numberType.sqrt(arg);
    }
}