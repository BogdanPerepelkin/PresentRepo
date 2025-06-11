package expression;

import expression.exceptions.NegativeDistanceException;
import expression.exceptions.OverflowIntBinException;
import expression.parser.GeneralInterface;

public class Area extends BinCalc {
    public Area(GeneralInterface left, GeneralInterface right) {
        super(left, right, "◣");
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (x < 0 || y < 0) {
            throw new NegativeDistanceException(x, y, this.type);
        } else if (y != 0 && x - Integer.MAX_VALUE / y > Integer.MAX_VALUE / y) {
            throw new OverflowIntBinException(x, y, this.type);
        }
        if (x % 2 == 0) {
            return x / 2 * y;
        } else if (y % 2 == 0) {
            return x * (y / 2);
        } else {
            int tmpL;
            int tmpR;
            if (x > y) {
                tmpL = (x - 1) / 2;
                tmpR = y;
            } else {
                tmpL  = (y - 1) / 2;
                tmpR = x;
            }
            if (tmpL - Integer.MAX_VALUE / tmpR > Integer.MAX_VALUE / tmpR) {
                throw new OverflowIntBinException(x, y, this.type);
            }
            if (tmpL > Integer.MAX_VALUE - tmpR) {
                throw new OverflowIntBinException(x, y, this.type);
            }
            return tmpL * tmpR + tmpR / 2;
        }
    }

    @Override
    public Number calculateLong(Number left, Number right) {
        return left.longValue() + right.longValue();
    }
}