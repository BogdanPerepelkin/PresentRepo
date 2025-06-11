package common.expression;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Basic arithmetics.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ArithmeticBuilder implements OperationsBuilder {
    private final BaseVariant variant;
    private final F neg;
    private final F add;
    private final F sub;
    private final F mul;
    private final F div;

    public ArithmeticBuilder(final boolean varargs, final List<String> variables) {
        variant = new BaseVariant(varargs);
        variables.forEach(this::variable);

        //noinspection Convert2MethodRef
        variant.infix("+", 100, (a, b) -> a + b);
        variant.infix("-", 100, (a, b) -> a - b);
        variant.infix("*", 200, (a, b) -> a * b);
        variant.infix("/", 200, (a, b) -> a / b);
        variant.unary("negate", a -> -a);

        add = f("+", 2);
        sub = f("-", 2);
        mul = f("*", 2);
        div = f("/", 2);
        neg = f("negate", 1);
        
        basicTests();
    }

    public void basicTests() {
        final List<F> ops = List.of(neg, add, sub, mul, div);
        variant.tests(() -> Stream.of(
                Stream.of(variant.c()),
                variant.getVariables().stream(),
                ops.stream().map(F::c),
                ops.stream().map(F::v),
                ops.stream().map(F::r),
                ops.stream().map(F::r),
                Stream.of(
                    div.f(neg.r(), r()),
                    div.f(r(), mul.r()),
                    add.f(add.f(mul.r(), mul.r()), mul.r()),
                    sub.f(add.f(mul.r(), mul.f(r(), mul.f(r(), mul.r()))), mul.r())
                )
        ).flatMap(Function.identity()));
    }

    @Override
    public void constant(final String name, final String alias, final double value) {
        alias(name, alias);
        final BaseTester.Func expr = vars -> value;
        variant.nullary(name, expr);
        final Expr constant = Expr.nullary(name, expr);
        variant.tests(() -> Stream.of(
                neg.f(constant),
                add.f(constant, r()),
                sub.f(r(), constant),
                mul.f(r(), constant),
                div.f(constant, r())
        ));
    }

    @Override
    public void unary(final String name, final String alias, final DoubleUnaryOperator op) {
        variant.unary(name, op);
        variant.alias(name, alias);
        unaryTests(name);
    }

    private void unaryTests(final String name) {
        final F op = f(name, 1);
        variant.tests(() -> Stream.of(
                op.c(),
                op.v(),
                op.f(sub.r()),
                op.f(add.r()),
                op.f(div.f(op.r(), add.r())),
                add.f(op.f(op.f(add.r())), mul.f(r(), mul.f(r(), op.r())))
        ));
    }

    @Override
    public void binary(final String name, final String alias, final DoubleBinaryOperator op) {
        variant.binary(name, op);
        variant.alias(name, alias);
        binaryTests(name);
    }

    private void binaryTests(final String name) {
        final F op = f(name, 2);
        variant.tests(() -> Stream.of(
                op.c(),
                op.v(),
                op.r(),
                op.f(neg.r(), add.r()),
                op.f(sub.r(), neg.r()),
                op.f(neg.r(), op.r()),
                op.f(op.r(), neg.r())
        ));
    }
    
    private record F(String name, int arity, BaseVariant variant) {
        public Expr f(final Expr... args) {
            assert arity < 0 || arity == args.length;
            return variant.f(name, args);
        }

        public Expr v() {
            return g(variant::v);
        }

        public Expr c() {
            return g(variant::c);
        }

        public Expr r() {
            return g(variant::r);
        }

        private Expr g(final Supplier<Expr> g) {
            return f(Stream.generate(g).limit(arity).toArray(Expr[]::new));
        }
    }
    
    private F f(final String name, final int arity) {
        return new F(name, arity, variant);
    }

    private Expr r() {
        return variant.r();
    }

    private Expr f(final String name, final Expr... args) {
        return variant.f(name, args);
    }

    @Override
    public void infix(final String name, final String alias, final int priority, final DoubleBinaryOperator op) {
        variant.infix(name, priority, op);
        variant.alias(name, alias);
        binaryTests(name);
    }


    @Override
    public void fixed(
            final String name,
            final String alias,
            final int arity,
            final BaseTester.Func f
    ) {
        variant.fixed(name, arity, f);
        variant.alias(name, alias);

        if (arity == 1) {
            unaryTests(name);
        } else if (arity == 2) {
            binaryTests(name);
        } else if (arity == 3) {
            final F op = f(name, 3);
            variant.tests(() -> {
                final Expr e1 = op.c();
                final Expr e2 = op.v();
                final Expr e3 = op.f(add.r(), sub.r(), mul.r());
                return Stream.of(
                        op.f(variant.c(), r(), r()),
                        op.f(r(), variant.c(), r()),
                        op.f(r(), r(), variant.c()),
                        op.f(variant.v(), mul.v(), mul.v()),
                        op.f(mul.v(), variant.v(), mul.v()),
                        op.f(mul.v(), r(), mul.v()),
                        op.r(),
                        e1,
                        e2,
                        e3,
                        op.f(e1, e2, e3)
                );
            });
        } else if (arity == 4) {
            final F op = f(name, 4);
            variant.tests(() -> {
                final Expr e1 = op.c();
                final Expr e2 = op.v();
                final Expr e3 = op.r();
                final Expr e4 = op.f(add.r(), sub.r(), mul.r(), div.r());
                return Stream.of(
                        op.r(),
                        op.r(),
                        op.r(),
                        e1,
                        e2,
                        e3,
                        e4,
                        op.f(e1, e2, e3, e4)
                );
            });
        } else {
            variant.tests(() -> Stream.concat(
                    Stream.of(
                            f(name, arity, variant::c),
                            f(name, arity, variant::v)
                    ),
                    IntStream.range(0, 10).mapToObj(i -> f(name, arity, variant::r))
            ));
        }
    }

    private Expr f(final String name, final int arity, final Supplier<Expr> generator) {
        return f(name, Stream.generate(generator).limit(arity).toArray(Expr[]::new));
    }

    @Override
    public void any(
            final String name,
            final String alias,
            final int minArity,
            final int fixedArity,
            final BaseTester.Func f
    ) {
        variant.alias(name, alias);
        variant.any(name, minArity, fixedArity, f);

        if (variant.hasVarargs()) {
            final F op = f(name, -1);
            variant.tests(() -> Stream.of(
                    op.f(r()),
                    op.f(r(), r()),
                    op.f(r(), r(), r()),
                    op.f(r(), r(), r(), r()),
                    op.f(r(), r(), r(), r(), r()),
                    op.f(add.r(), r()),
                    op.f(r(), r(), sub.r())
            ));
        }

        variant.tests(() -> IntStream.range(1, 10)
                        .mapToObj(i -> f(name, variant.hasVarargs() ? i : fixedArity, variant::r)));
    }

    @Override
    public void variable(final String name) {
        variant.variable(name, variant.getVariables().size());
    }

    @Override
    public void alias(final String name, final String alias) {
        variant.alias(name, alias);
    }

    @Override
    public BaseVariant variant() {
        return variant;
    }
}
