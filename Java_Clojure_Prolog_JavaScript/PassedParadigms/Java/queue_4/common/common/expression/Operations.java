package common.expression;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Known expression operations.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public enum Operations {
    ;

    public static final Operation ARITH = builder -> {
        builder.ops.alias("negate", "Negate");
        builder.ops.alias("+", "Add");
        builder.ops.alias("-", "Subtract");
        builder.ops.alias("*", "Multiply");
        builder.ops.alias("/", "Divide");
    };

    public static final Operation NARY_ARITH = builder -> {
        builder.ops.unary("negate", "Negate", a -> -a);

        builder.ops.any("+", "Add", 0, 2, arith(0, Double::sum));
        builder.ops.any("-", "Subtract", 1, 2, arith(0, (a, b) -> a - b));
        builder.ops.any("*", "Multiply", 0, 2, arith(1, (a, b) -> a * b));
        builder.ops.any("/", "Divide", 1, 2, arith(1, (a, b) -> a / b));
    };

    // === Common

    public static Operation unary(final String name, final String alias, final DoubleUnaryOperator op) {
        return builder -> builder.ops.unary(name, alias, op);
    }

    public static Operation binary(final String name, final String alias, final DoubleBinaryOperator op) {
        return builder -> builder.ops.binary(name, alias, op);
    }

    public static BaseTester.Func arith(final double zero, final DoubleBinaryOperator f) {
        return args -> args.length == 0 ? zero
                : args.length == 1 ? f.applyAsDouble(zero, args[0])
                : Arrays.stream(args).reduce(f).orElseThrow();
    }


    // === More common

    public record Op(String name, String alias, int minArity, BaseTester.Func f) {
        public Operation fix(final int arity) {
            return fix(arity, name.charAt(0) <= 0xff);
        }

        public Operation fix(final int arity, final boolean unicode) {
            assert arity >= minArity;
            return fixed(name + (char) ((unicode ? '0' : '₀') + arity), alias + arity, arity, f);
        }

        public Operation any(final int fixedArity) {
            return checker -> checker.ops.any(name, alias, minArity, fixedArity, f);
        }
    }

    public static Op op(final String name, final String alias, final int minArity, final BaseTester.Func f) {
        return new Op(name, alias, minArity, f);
    }

    public static Op opS(final String name, final String alias, final int minArity, final ToDoubleFunction<DoubleStream> f) {
        return op(name, alias, minArity, args -> f.applyAsDouble(Arrays.stream(args)));
    }

    public static Op opO(final String name, final String alias, final int minArity, final Function<DoubleStream, OptionalDouble> f) {
        return opS(name, alias, minArity, f.andThen(OptionalDouble::orElseThrow)::apply);
    }

    public static Operation fixed(final String name, final String alias, final int arity, final BaseTester.Func f) {
        return builder -> builder.ops.fixed(name, alias, arity, f);
    }

    @SuppressWarnings("SameParameterValue")
    public static Operation range(final int min, final int max, final Op... ops) {
        final List<Operation> operations = IntStream.rangeClosed(min, max)
                .mapToObj(i -> Arrays.stream(ops).map(op -> op.fix(i)))
                .flatMap(Function.identity())
                .toList();
        return builder -> operations.forEach(op -> op.accept(builder));
    }

    @SuppressWarnings("SameParameterValue")
    public static Operation any(final int fixed, final Op... ops) {
        final List<Operation> operations = Arrays.stream(ops).map(op -> op.any(fixed)).toList();
        return builder -> operations.forEach(op -> op.accept(builder));
    }

    public static Operation infix(
            final String name,
            final String alias,
            final int priority,
            final DoubleBinaryOperator op
    ) {
        return checker -> checker.ops.infix(name, alias, priority, op);
    }


    // === Variables
    public static final Operation VARIABLES = builder ->
            Stream.of("y", "z", "t").forEach(builder.ops::variable);


    // === TauPhi

    public static Operation constant(final String name, final double value) {
        return builder -> builder.ops.constant(name, name, value);
    }

    public static final Operation TAU = constant("tau", Math.PI * 2);
    public static final Operation PHI = constant("phi", Math.sqrt((1 + Math.sqrt(5)) / 2));


    // === Less, Greater

    private static Op compare(final String name, final String alias, final IntPredicate p) {
        return op(name, alias, 1, args -> IntStream.range(1, args.length)
                .allMatch(i -> p.test(args[i - 1] == args[i] ? 0 : Double.compare(args[i - 1], args[i])))
                ? 1 : 0
        );
    }

    public static final Op LESS = compare("less", "Less", c -> c < 0);
    public static final Op GREATER = compare("greater", "Greater", c -> c > 0);


    // === LessEq, GreaterEq
    public static final Op LESS_EQ = compare("lessEq", "LessEq", c -> c <= 0);
    public static final Op GREATER_EQ = compare("greaterEq", "GreaterEq", c -> c >= 0);


    // === Sinh, Cosh
    public static final Operation SINH = unary("sinh", "Sinh", Math::sinh);
    public static final Operation COSH = unary("cosh", "Cosh", Math::cosh);


    // === Pow, Log
    public static final Operation POW = binary("pow", "Power", Math::pow);
    public static final Operation LOG = binary("log", "Log", (a, b) -> Math.log(Math.abs(b)) / Math.log(Math.abs(a)));


    // === Normal (Multivariate normal distribution)
    public static final Op NORMAL = op("normal", "Normal", 1, args ->
            Math.exp(Arrays.stream(args).map(a -> a * a).sum() / -2) / Math.pow(2 * Math.PI, args.length / 2.0));


    // === Poly
    public static final Op POLY = op("poly", "Poly", 0, args -> {
        double[] pows = DoubleStream.iterate(1, p -> p * args[0]).limit(args.length - 1).toArray();
        return IntStream.range(0, args.length - 1).mapToDouble(i -> pows[i] * args[i + 1]).sum();
    });


    // === Clamp, wrap
    public static final Operation CLAMP = fixed("clamp", "Clamp", 3, args ->
            args[1] <= args[2] ? Math.min(Math.max(args[0], args[1]), args[2]) : Double.NaN);
    public static final Operation SOFT_CLAMP = fixed("softClamp", "SoftClamp", 4, args ->
            args[1] <= args[2] && args[3] > 0
                    ? args[1] + (args[2] - args[1]) / (1 + Math.exp(args[3] * ((args[2] + args[1]) / 2 - args[0])))
                    : Double.NaN);


    // === SumCb

    public static double sumCb(final double... args) {
        return Arrays.stream(args).map(a -> a * a * a).sum();
    }

    private static double meanCb(final double[] args) {
        return sumCb(args) / args.length;
    }

    public static final Op SUM_CB = op("sumcb", "SumCb", 0, Operations::sumCb);
    public static final Op MEAN_CB = op("meancb", "MeanCb", 1, Operations::meanCb);
    public static final Op RMC = op("rmc", "RMC", 1, args -> Math.cbrt(meanCb(args)));
    public static final Op CB_MAX = op("cbmax", "CbMax", 1, args -> args[0] * args[0] * args[0] / sumCb(args));
}
