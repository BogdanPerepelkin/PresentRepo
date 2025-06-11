package expression.exceptions;

import expression.GeneralInterface;
import expression.Multiply;
import expression.exceptions.accum.OverflowIntBinException;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(GeneralInterface left, GeneralInterface right) {
        super(left, right);
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (x != 0 && y != 0 && (x > 0 && y > 0 && x > Integer.MAX_VALUE / y
                || x > 0 && y < 0 && y < Integer.MIN_VALUE / x
                || x < 0 && y > 0 && x < Integer.MIN_VALUE / y
                || x < 0 && y < 0 && x < Integer.MAX_VALUE / y)) {
            throw new OverflowIntBinException(x, y, this.type);
        }
        return left.intValue() * right.intValue();
    }
}