package expression;

import java.util.Objects;

public class Variable implements Expression, LongTripleExpression, TripleExpression {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public int evaluate(int arg) {
        return arg;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        if (var.equals("x")) {
            return x;
        } else if (var.equals("y")) {
            return y;
        } else {
            return z;
        }
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        if (var.equals("x")) {
            return x;
        } else if (var.equals("y")) {
            return y;
        } else {
            return z;
        }
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Variable that) {
            return this.var.equals(that.var);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }
}