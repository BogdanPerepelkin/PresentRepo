package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.util.function.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Operations {
    public static final Operation NEGATE = unary("-", 1, a -> -a);
    @SuppressWarnings("Convert2MethodRef")
    public static final Operation ADD       = binary("+", 1600, (a, b) -> a + b);
    public static final Operation SUBTRACT  = binary("-", 1602, (a, b) -> a - b);
    public static final Operation MULTIPLY  = binary("*", 2001, (a, b) -> a * b);
    public static final Operation DIVIDE    = binary("/", 2002, (a, b) -> b == 0 ? Reason.DBZ.error() : a / b);

    private Operations() {
    }

    @FunctionalInterface
    public interface Operation extends Consumer<ParserTester> {}

    public static Operation unary(final String name, final int priority, final LongUnaryOperator op) {
        return unary(name, priority, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String name, final int priority, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(name, priority, op);
    }

    public static Operation binary(final String name, final int priority, final LongBinaryOperator op) {
        return tests -> tests.binary(name, priority, op);
    }

    public static <E extends ToMiniString, C> Operation kind(
            final ExpressionKind<E, C> kind,
            final ParserTestSet.Parser<E> parser
    ) {
        return factory -> factory.kind(kind, parser);
    }
}
