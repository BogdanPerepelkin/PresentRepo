package expression;

public class Subtract extends BinCalc {
    public Subtract(Expression left, Expression right) {
        super(left, right, "-");
    }

    @Override
    public int evaluate(int arg) {
        return left.evaluate(arg) - right.evaluate(arg);
    }
}