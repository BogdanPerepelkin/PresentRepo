package expression.generic.genericParser.operations;


import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.NumberInterface;

public class Sqrt<T> extends UnCalc<T> {
    public Sqrt(GenericExpression<T> obj, NumberInterface<T> numberType) {
        super(obj, numberType);
    }

    public String toString() {
        return "√(" + obj.toString() + ")";
    }

    @Override
    public T calculate(T arg) {
        return numberType.sqrt(arg);
    }
}