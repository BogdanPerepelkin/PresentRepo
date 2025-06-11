package expression;

import java.util.Objects;

public abstract class BinCalc implements TripleExpression, LongTripleExpression, Expression {
    final Expression left;
    final Expression right;
    private final String type;

    public BinCalc(Expression left, Expression right, String type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + type + " " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof BinCalc that) {
            return left.equals(that.left) && right.equals(that.right) && type.equals(that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, type);
    }
}











