package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.util.function.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Operations {
    // === Base

    public static final Operation NEGATE = unary("-", 1, a -> -a);
    @SuppressWarnings("Convert2MethodRef")
    public static final Operation ADD       = binary("+", 1600, (a, b) -> a + b);
    public static final Operation SUBTRACT  = binary("-", 1602, (a, b) -> a - b);
    public static final Operation MULTIPLY  = binary("*", 2001, (a, b) -> a * b);
    public static final Operation DIVIDE    = binary("/", 2002, (a, b) -> b == 0 ? Reason.DBZ.error() : a / b);

    // === CmpEq

    public static final Operation LESS = binary("<", 202, (a, b) -> a < b ? 1 : 0);
    public static final Operation GREATER = binary(">", 202, (a, b) -> a > b ? 1 : 0);
    public static final Operation LESS_EQ = binary("<=", 202, (a, b) -> a <= b ? 1 : 0);
    public static final Operation GREATER_EQ = binary(">=", 202, (a, b) -> a >= b ? 1 : 0);
    public static final Operation EQUAL = binary("==", 22, (a, b) -> a == b ? 1 : 0);
    public static final Operation NOT_EQUAL = binary("!=", 22, (a, b) -> a != b ? 1 : 0);

    // === Min, Max

    public static final Operation MIN = binary("<?", 401, Math::min);
    public static final Operation MAX = binary(">?", 401, Math::max);

    // ===  Geometry

    public static final Reason NEGATIVE_SIDE = new Reason("Negative side");

    public static long area(final long a, final long b) {
        return a < 0 || b < 0 ? NEGATIVE_SIDE.error() : a * b / 2;
    }

    public static long perimeter(final long a, final long b) {
        return a < 0 || b < 0 ? NEGATIVE_SIDE.error() : (a + b) * 2;
    }

    public static final Operation AREA = binary("area", 22, Operations::area);
    public static final Operation PERIMETER = binary("perimeter", 22, Operations::perimeter);

    // === Sqrt

    private static final Reason NEGATIVE_SQRT = new Reason("Square root of negative value");
    public static final Operation SQRT = unary("sqrt", 1, NEGATIVE_SQRT.less(0, a -> (long) Math.sqrt(a)));

    // === Common

    private Operations() {
    }

    public static Operation unary(final String name, final int priority, final LongUnaryOperator op) {
        return unary(name, priority, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String left, final String right, final LongUnaryOperator op) {
        return unary(left, right, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String name, final int priority, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(name, priority, op);
    }

    public static Operation unary(final String left, final String right, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(left, right, op);
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

    @FunctionalInterface
    public interface Operation extends Consumer<ParserTester> {}
}
