package expression;

import expression.parser.GeneralInterface;

import java.util.Objects;

public class Variable extends GeneralInterface {
    private final String fullVar;
    private final char realVar;

    public Variable(String var) {
        this.fullVar = var;
        this.realVar = var.charAt(var.length() - 1);
    }

    @Override
    public String toString() {
        return fullVar;
    }

    @Override
    public int evaluate(int arg) {
        return arg;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch (realVar) {
            case 'x' -> x;
            case 'y' -> y;
            default -> z;
        };
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return switch (realVar) {
            case 'x' -> x;
            case 'y' -> y;
            default -> z;
        };
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Variable that) {
            return this.fullVar.equals(that.fullVar);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullVar);
    }
}