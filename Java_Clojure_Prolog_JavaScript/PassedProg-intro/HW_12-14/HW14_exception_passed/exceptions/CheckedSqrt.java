package expression.exceptions;

import expression.UnCalc;
import expression.parser.GeneralInterface;

public class CheckedSqrt extends UnCalc {
    public CheckedSqrt(GeneralInterface obj) {
        super(obj);
    }

    public static int sqrt(int n) {
        if (n < 0) {
            throw new DomainDefenitionException(n, "√");
        }
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        }
        int l = 1;
        int r = n;
        int answ = l;
        while (r - l >= 0) {
            int mid = l + (r - l) / 2;
            if (mid > n / mid) {
                r = mid - 1;
            } else {
                l = mid + 1;
                answ = mid;
            }
        }
        return answ;
    }

    @Override
    public String toString() {
        return "√(" + obj.toString() + ")";
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return sqrt(obj.evaluate(x, y, z));
    }
}