package expression;

public class Add extends BinCalc {
    public Add(Expression left, Expression right) {
        super(left, right, "+");
    }

    @Override
    public int evaluate(int arg) {
        return left.evaluate(arg) + right.evaluate(arg);
    }
}