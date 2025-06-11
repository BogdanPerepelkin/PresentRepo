package expression;

import expression.parser.GeneralInterface;

public class Multiply extends BinCalc {
    public Multiply(GeneralInterface left, GeneralInterface right) {
        super(left, right, "*");
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        return left.intValue() * right.intValue();
    }

    @Override
    public Number calculateLong(Number left, Number right) {
        return left.longValue() * right.longValue();
    }
}