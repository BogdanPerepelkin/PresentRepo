package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.math.BigInteger;
import java.util.function.*;
import java.util.stream.LongStream;

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


    // === Pow, Log
    public static final Operation POW_O = binary("**", 2402, (a, b) ->
            b < 0 ? 1 : BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(1L << 32)).intValue());
    public static final Operation LOG_O = binary("//", 2402, (a, b) ->
            a == 0 && b > 0 ? Integer.MIN_VALUE :
            a <= 0 || b <= 0 || a == 1 && b == 1 ? 0 :
            a > 1 && b == 1 ? Integer.MAX_VALUE :
            LongStream.iterate(b, v -> v <= a, v -> v * b).count()
    );

    private static final Reason INVALID_POW = new Reason("Invalid power");
    private static long powC(final long a, final long b) {
        if (b < 0 || a == 0 && b == 0) {
            return INVALID_POW.error();
        }
        if (Math.abs(a) > 1 && b > 32) {
            return Reason.OVERFLOW.error();
        }
        final BigInteger result = BigInteger.valueOf(a).pow((int) b);
        if (result.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 || BigInteger.valueOf(Integer.MAX_VALUE).compareTo(result) < 0) {
            return Reason.OVERFLOW.error();
        }
        return result.intValue();
    }

    // === Square
    public static final Operation SQUARE = unary("²", 2, a -> a * a);
    public static final Operation TETRA_2 = unary("²", 1, a -> powC(a, a));

    // === Cube
    public static final Operation CUBE = unary("³", 2, (a, c) -> c.applyAsInt(a * a) * a);
    public static final Operation TETRA_3 = unary("³", 1, a -> powC(a, powC(a, a)));

    // === Factorial
    public static final Operation FACTORIAL_O = unary("!", 0, (n, c) ->
            LongStream.range(1, Math.min(Math.abs(n) + 1, 0x3f)).reduce(1, (a, b) -> (a * b) & 0xfffffffffL));
    private static final Reason NEGATIVE_FACTORIAL = new Reason("Factorial of negative value");
    public static final Operation FACTORIAL_C = unary("!", 0, NEGATIVE_FACTORIAL.less(0, n ->
            LongStream.rangeClosed(2, n).reduce(1, (a, b) -> Reason.overflow(a * b))));
    public static final Operation PRE_FACTORIAL = unary("!", 1, n -> {
        int i = 1;
        while (n > 0) {
            n /= ++i;
        }
        return i - 1;
    });

    // === Sqrt
    public static final Operation SQRT_O = unary("√", 1, a -> (long) Math.sqrt(a));
    private static final Reason NEGATIVE_SQRT = new Reason("Square root of negative value");
    public static final Operation SQRT_C = unary("√", 1, NEGATIVE_SQRT.less(0, a -> (long) Math.sqrt(a)));

    // === Cbrt
    public static final Operation CBRT = unary("∛", 1, a -> (long) Math.cbrt(a));


    // === GCD, LCM

    public static final Operation GCD = binary("gcd", 601, Operations::gcd);
    public static final Operation LCM = binary("lcm", 601, (a, b) -> {
        if (a == 0 && b == 0) {
            return 0;
        }
        return a * b / gcd(a, b);
    });

    private static int gcd(final long a, final long b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    // === Geometry
    public static final Reason NEGATIVE_SIDE = new Reason("Negative side");
    public static final Operation AREA = binary("◣", 22, (a, b) -> a < 0 || b < 0 ? NEGATIVE_SIDE.error() : a * b / 2);
    public static final Operation PERIMETER = binary("▯", 22, (a, b) -> a < 0 || b < 0 ? NEGATIVE_SIDE.error() : (a + b) * 2);


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
