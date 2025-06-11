package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;


public class Variable<E> implements GenericExpression<E> {
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
    public E evaluate(E x, E y, E z) {
        return switch (realVar) {
            case 'x' -> x;
            case 'y' -> y;
            default -> z;
        };
    }
}