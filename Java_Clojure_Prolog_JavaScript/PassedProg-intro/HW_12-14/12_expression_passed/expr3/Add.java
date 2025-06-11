package expression;

public class Add extends BinCalc {
    public Add(Expression left, Expression right) {
        super(left, right, "+");
    }

    @Override
    public int evaluate(int arg) {
        return left.evaluate(arg) + right.evaluate(arg);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return ((TripleExpression)left).evaluate(x, y, z) + ((TripleExpression)right).evaluate(x, y, z);
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return ((LongTripleExpression)left).evaluateL(x, y, z) + ((LongTripleExpression)right).evaluateL(x, y, z);
    }
}