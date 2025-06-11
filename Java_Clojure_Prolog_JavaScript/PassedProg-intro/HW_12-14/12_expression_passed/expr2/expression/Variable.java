package expression;

public class Variable implements Expression {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "x";
    }

    @Override
    public int evaluate(int arg) {
        return arg;
    }

    @Override
    public boolean equals(Expression exp) {
        return toString().equals(exp.toString());
    }

    @Override
    public Expression getLeft() {
        return this;
    }

    @Override
    public Expression getRight() {
        return this;
    }

}