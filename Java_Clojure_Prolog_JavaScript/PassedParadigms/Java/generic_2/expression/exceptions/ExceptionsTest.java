package expression.exceptions;

import base.Selector;
import expression.TripleExpression;

import static expression.parser.Operations.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExceptionsTest {
    private static final ExpressionParser PARSER = new ExpressionParser();
    private static final Operation TRIPLE = kind(TripleExpression.KIND, (expr, variables) -> PARSER.parse(expr));

    // === Parens

    public static final Operation PARENS = tester -> tester.parens("{", "}", "[", "]");



    // === Common
    public static final Selector SELECTOR = Selector.composite(ExceptionsTest.class, ExceptionsTester::new, "easy", "hard")
            .variant("Base", TRIPLE, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("3233", PARENS, AREA, PERIMETER)
            .variant("3435", PARENS, AREA, PERIMETER, SQRT)
            .variant("3637", PARENS, LESS, LESS_EQ, GREATER, GREATER_EQ, EQUAL, NOT_EQUAL)
            .variant("3839", PARENS, LESS, LESS_EQ, GREATER, GREATER_EQ, EQUAL, NOT_EQUAL, MIN, MAX)
            .selector();

    private ExceptionsTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
