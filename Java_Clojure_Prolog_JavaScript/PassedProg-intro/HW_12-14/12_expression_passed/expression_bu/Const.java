package expression;

public class Const implements Expression {
    private final int con;

    public Const(int con) {
        this.con = con;
    }

    @Override
    public String toString() {
        return Integer.toString(con);
    }

    @Override
    public int evaluate(int arg) {
        return con;
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