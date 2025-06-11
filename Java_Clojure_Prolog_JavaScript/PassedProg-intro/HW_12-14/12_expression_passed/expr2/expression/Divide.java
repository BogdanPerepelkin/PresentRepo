package expression;

public class Divide extends BinCalc {
    public Divide(Expression left, Expression right) {
        super(left, right, "/");
    }

    @Override
    public int evaluate(int arg) {
        return left.evaluate(arg) / right.evaluate(arg);
    }
}