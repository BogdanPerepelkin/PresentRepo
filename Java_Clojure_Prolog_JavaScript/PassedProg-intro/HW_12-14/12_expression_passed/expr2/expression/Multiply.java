package expression;

public class Multiply extends BinCalc {
    public Multiply(Expression left, Expression right) {
        super(left, right, "*");
    }

    @Override
    public int evaluate(int arg) {
        return left.evaluate(arg) * right.evaluate(arg);
    }
}