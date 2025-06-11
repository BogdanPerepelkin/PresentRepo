package expression.exceptions;

import expression.GeneralInterface;
import expression.Subtract;
import expression.exceptions.accum.OverflowIntBinException;

public class CheckedSubtract extends Subtract {
    public CheckedSubtract(GeneralInterface left, GeneralInterface right) {
        super(left, right);
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (x >= 0 && y <= 0 && x > Integer.MAX_VALUE + y
                || x <= 0 && y >= 0 && x < Integer.MIN_VALUE + y) {
            throw new OverflowIntBinException(x, y, this.type);
        }
        return left.intValue() - right.intValue();
    }
}