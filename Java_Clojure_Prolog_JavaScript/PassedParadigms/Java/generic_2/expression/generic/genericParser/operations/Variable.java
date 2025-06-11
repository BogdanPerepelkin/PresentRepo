package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;


public class Variable<T> implements GenericExpression<T> {
    private final String fullVar;
    private final char realVar;

    public Variable(String var) {
        this.fullVar = var;
        this.realVar = var.charAt(var.length() - 1);
    }

    public String toString() {
        return fullVar;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch (realVar) {
            case 'x' -> x;
            case 'y' -> y;
            default -> z;
        };
    }
}