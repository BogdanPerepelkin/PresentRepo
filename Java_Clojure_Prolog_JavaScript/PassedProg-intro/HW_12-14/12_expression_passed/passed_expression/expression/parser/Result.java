package expression.parser;

import expression.BinCalc;
import expression.Expression;

public class Result extends BinCalc {

    public Result(Expression left, Expression right, String type) {
        super(left, right, type);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return 0;
    }

    @Override
    public int evaluate(int x) {
        return 0;
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return 0;
    }
}