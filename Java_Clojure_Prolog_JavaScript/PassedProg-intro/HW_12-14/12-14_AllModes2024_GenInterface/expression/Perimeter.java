package expression;

import expression.exceptions.NegativeDistanceException;
import expression.exceptions.OverflowIntBinException;
import expression.parser.GeneralInterface;

public class Perimeter extends BinCalc {
    public Perimeter(GeneralInterface left, GeneralInterface right) {
        super(left, right, "▯");
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (x < 0 || y < 0) {
            throw new NegativeDistanceException(x, y, this.type);
        } else if (x > Integer.MAX_VALUE / 2 - y) {
            throw new OverflowIntBinException(x, y, this.type);
        }
        return 2 * (x + y);
    }

    @Override
    public Number calculateLong(Number left, Number right) {
        return left.longValue() + right.longValue();
    }
}