package expression;

public class Add extends BinCalc {
    public Add(Expression left, Expression right) {
        super(left, right, "+");
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        return left.intValue() + right.intValue();
    }

    @Override
    public Number calculateLong(Number left, Number right) {
        return left.longValue() + right.longValue();
    }
}