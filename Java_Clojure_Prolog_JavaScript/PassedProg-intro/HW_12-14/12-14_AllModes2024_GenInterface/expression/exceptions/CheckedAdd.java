package expression.exceptions;

import expression.Add;
import expression.BinCalc;
import expression.parser.GeneralInterface;

public class CheckedAdd extends Add {
    public CheckedAdd(GeneralInterface left, GeneralInterface right) {
        super(left, right);
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (x > 0 && y > 0 && x > Integer.MAX_VALUE - y
                || x < 0 && y < 0 && x < Integer.MIN_VALUE - y) {
            throw new OverflowIntBinException(x, y, this.type);
        }
        return left.intValue() + right.intValue();
    }
}