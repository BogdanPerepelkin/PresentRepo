package expression;


public class Subtract extends BinCalc {
    public Subtract(GeneralInterface left, GeneralInterface right) {
        super(left, right, "-");
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        return left.intValue() - right.intValue();
    }

    @Override
    public Number calculateLong(Number left, Number right) {
        return left.longValue() - right.longValue();
    }
}