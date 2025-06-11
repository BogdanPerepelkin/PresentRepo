package expression;

public abstract class BinCalc implements Expression{
    final Expression left;
    final Expression right;
    private final String type;

    public BinCalc(Expression left, Expression right, String type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public Expression getLeft() {
        return left;
    }

    @Override
    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("(");
        return out.append(left.toString()).append(" ").append(type).append(" ")
                .append(right.toString()).append(")").toString();
    }

    @Override
    public boolean equals(Expression exp) {
        return left.equals(exp.getLeft()) && right.equals(exp.getRight());
    }
}











