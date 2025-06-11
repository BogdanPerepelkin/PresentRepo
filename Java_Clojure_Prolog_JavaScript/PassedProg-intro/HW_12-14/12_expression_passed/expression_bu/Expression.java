package expression;

public interface Expression {
    String toString();
    int evaluate(int arg);
    boolean equals(Expression exp);
    Expression getLeft();
    Expression getRight();
}