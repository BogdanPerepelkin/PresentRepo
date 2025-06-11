package expression.generic;

import expression.exceptions.accum.EvaluateException;
import expression.generic.genericParser.GenericExpression;
import expression.generic.genericParser.GenericParser;
import expression.generic.genericParser.NumberInterface;
import expression.generic.genericParser.types.GenBigInteger;
import expression.generic.genericParser.types.GenDouble;
import expression.generic.genericParser.types.GenInteger;


public class GenericTabulator implements Tabulator {
    public static void main(String[] args) {
        NumberInterface<?> numberType = defineMode(args[0]);
        genTabulate(numberType, args[1], -2, 2, -2, 2, -2, 2, true);
    }

    @Override
    public Object[][][] tabulate(String mode, String inputExpression,
                                 int x1,  int x2, int y1, int y2, int z1, int z2) throws Exception {
        return genTabulate(defineMode(mode), inputExpression, x1, x2, y1, y2, z1, z2, false);
    }

    public static <T> Object[][][] genTabulate(NumberInterface<T> numberType, String inputExpression,
                                        int x1, int x2, int y1, int y2, int z1, int z2, boolean flag) {
        GenericParser<T> parser = new GenericParser<T>(numberType);
        GenericExpression<T> expression = parser.parse(inputExpression);
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int i = 0; i <= x2 - x1; i++) {
            for (int j = 0; j <= y2 - y1; j++) {
                for (int g = 0; g <= z2 - z1; g++) {
                    T eval;
                    try {
                        eval = expression.evaluate(
                                numberType.int2Type(x1 + i),
                                numberType.int2Type(y1 + j),
                                numberType.int2Type(z1 + g));
                    } catch (EvaluateException e) {
                        eval = null;
                    }
                    if (flag) {
                        System.out.println(eval);
                    } else {
                        table[i][j][g] = eval;
                    }
                }
            }
        }
        return table;
    }

    public static NumberInterface<?> defineMode(String s) {
        return switch (s) {
            case "i" -> new GenInteger(true);
            case "d" -> new GenDouble();
            case "bi" -> new GenBigInteger();
            default -> null;
        };
    }
}





















