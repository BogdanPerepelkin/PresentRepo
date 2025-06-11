package expression.generic;

import expression.exceptions.accum.EvaluateException;
import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.GenericParser;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.types.GenInteger;

import java.util.Arrays;

public class TestOwnGeneric {
    public static <E> void main(String[] args) throws Exception {
        GenericTabulator tabulator = new GenericTabulator();
        Object[][][] table;
        table = tabulator.tabulate("i", "4 area 100 ", 1, 2, 1, 2, 1, 2);
        GenInteger nT = new GenInteger(true);
        GenericParser<?> parser = new GenericParser<>(new GenInteger(true));
        /*
        ({(x area 1320926896) * [x - x]} + (-[1857063605] / (364060056 - z)))
         */
        String expression = "(x area 1320926896) ";
        System.out.println(Integer.MAX_VALUE);
        GenericExpression<E> genExpr = (GenericExpression<E>) parser.parse(expression);
        System.out.println(genExpr);
        E var = count(genExpr, (NumberInterface<E>) nT);
        System.out.println(var);
    }

    public static <E> E count(GenericExpression<E> expr, NumberInterface<E> nT) {
        return expr.evaluate(
                nT.int2Type(3),
                nT.int2Type(7),
                nT.int2Type(0)
        );
    }
}