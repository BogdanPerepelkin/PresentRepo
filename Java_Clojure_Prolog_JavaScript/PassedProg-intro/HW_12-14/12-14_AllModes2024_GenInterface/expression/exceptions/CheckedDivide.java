package expression.exceptions;

import expression.BinCalc;
import expression.Divide;
import expression.parser.GeneralInterface;

public class CheckedDivide extends Divide {
    public CheckedDivide(GeneralInterface left, GeneralInterface right) {
        super(left, right);
    }

    @Override
    public Number calculateInt(Number left, Number right) {
        int x = left.intValue();
        int y = right.intValue();
        if (y == 0) {
            throw new DBZException("divizion by zero");
        }else if (x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowIntBinException(x, y, this.type);
        }

        return left.intValue() / right.intValue();
    }
}