package expression.generic.genericParser.types;

import expression.exceptions.accum.*;
import expression.generic.genericParser.NumberInterface;

public class GenInteger implements NumberInterface<Integer> {
    final boolean fExcept;

    public GenInteger(boolean fExcept) {
        this.fExcept = fExcept;
    }

    @Override
    public Integer area(Integer x, Integer y) {
        if (fExcept && (x < 0 || y < 0)) {
            throw new NegativeDistanceException(x, y, "area");
        }
        int tmpL;
        int tmpR;
        if (x % 2 == 0 || y % 2 == 0) {
            if (x % 2 == 0) {
                tmpL = x / 2;
                tmpR = y;
            } else {
                tmpL = y / 2;
                tmpR = x;
            }
            if (fExcept && tmpR != 0 && (tmpL > Integer.MAX_VALUE / tmpR)) {
                throw new OverflowIntBinException(x, y, "area");
            }
            return tmpL * tmpR;
        } else {
            if (x > y) {
                tmpL = (x - 1) / 2;
                tmpR = y;
            } else {
                tmpL  = (y - 1) / 2;
                tmpR = x;
            }
            if (fExcept && tmpR != 0 && (tmpL > Integer.MAX_VALUE / tmpR)) {
                throw new OverflowIntBinException(x, y, "* in area");
            }
            if (fExcept && (tmpL * tmpR > Integer.MAX_VALUE - tmpR / 2)) {
                throw new OverflowIntBinException(x, y, "+ in area");
            }
            return tmpL * tmpR + tmpR / 2;
        }
    }

    public Integer perimeter(Integer left, Integer right) {
        int x = left;
        int y = right;
        if (fExcept && (x < 0 || y < 0)) {
            throw new NegativeDistanceException(x, y, "perimeter");
        } else if (fExcept && (x > Integer.MAX_VALUE / 2 - y)) {
            throw new OverflowIntBinException(x, y, "perimeter");
        }
        return 2 * (x + y);
    }

    @Override
    public Integer add(Integer left, Integer right) {
        if (fExcept) {
            int x = left;
            int y = right;
            if (x > 0 && y > 0 && x > Integer.MAX_VALUE - y
                    || x < 0 && y < 0 && x < Integer.MIN_VALUE - y) {
                throw new OverflowIntBinException(x, y, "+");
            }
        }
        return left + right;
    }

    @Override
    public Integer sub(Integer left, Integer right) {
        if (fExcept) {
            int x = left;
            int y = right;
            if (x >= 0 && y <= 0 && x > Integer.MAX_VALUE + y
                    || x <= 0 && y >= 0 && x < Integer.MIN_VALUE + y) {
                throw new OverflowIntBinException(x, y, "-");
            }
        }
        return left - right;
    }

    @Override
    public Integer mult(Integer left, Integer right) {
        if (fExcept) {
            int x = left;
            int y = right;
            if (x != 0 && y != 0 &&
                    (x > 0 && y > 0 && x > Integer.MAX_VALUE / y
                    || x > 0 && y < 0 && y < Integer.MIN_VALUE / x
                    || x < 0 && y > 0 && x < Integer.MIN_VALUE / y
                    || x < 0 && y < 0 && x < Integer.MAX_VALUE / y)) {
                throw new OverflowIntBinException(x, y, "*");
            }
        }
        return left * right;
    }

    @Override
    public Integer divide(Integer left, Integer right) {
        if (fExcept) {
            int x = left;
            int y = right;
            if (y == 0) {
                throw new DBZException("divizion by zero");
            } else if (x == Integer.MIN_VALUE && y == -1) {
                throw new OverflowIntBinException(x, y, "/");
            }
        }
        return left / right;
    }

    @Override
    public Integer str2Num(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer unaryMinus(Integer arg) {
        if (fExcept) {
            if (arg == Integer.MIN_VALUE) {
                throw new OverflowIntUnaryException(arg, "-");
            }
        }
        return (-1) * arg;
    }

    @Override
    public Integer sqrt(Integer arg) {
        if (fExcept && arg < 0) {
            throw new DomainDefenitionException(arg, "√");
        }
        if (arg == 0) {
            return 0;
        } else if (arg == 1) {
            return 1;
        }
        int l = 1;
        int r = arg;
        int answ = l;
        while (r - l >= 0) {
            int mid = l + (r - l) / 2;
            if (mid > arg / mid) {
                r = mid - 1;
            } else {
                l = mid + 1;
                answ = mid;
            }
        }
        return answ;
    }

    @Override
    public Integer int2Type(Integer arg) {
        return arg;
    }
}