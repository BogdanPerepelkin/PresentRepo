package common.expression;

import base.Asserts;
import base.ExtendedRandom;
import base.TestCounter;
import base.Tester;
import common.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Base expressions tester.
 *
 * @author Niyaz Nigmatullin
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class BaseTester<X, E extends Engine<X>> extends Tester {
    public static final int N = 128;
    public static final double EPS = 1e-3;
    public static int TESTS = 444;

    protected final E engine;
    /*package*/ final Language language;
    private final List<Runnable> stages = new ArrayList<>();

    private final List<Spoiler> spoilers;

    public static final List<Spoiler> STANDARD_SPOILERS = List.of(
            (unparsed, expr, random) -> unparsed,
            (unparsed, expr, random) -> addSpaces(unparsed, random)
    );

    protected BaseTester(
            final TestCounter counter,
            final E engine,
            final Language language,
            final List<Spoiler> spoilers
    ) {
        super(counter);
        this.engine = engine;
        this.language = language;
        this.spoilers = spoilers;
    }

    private static final Predicate<String> UNSAFE = Pattern.compile("[-\\p{Alnum}+*/.=&|^<>◀▶◁▷≤≥?⁰-⁹₀-₉]").asPredicate();

    private static boolean safe(final char ch) {
        return !UNSAFE.test("" + ch);
    }

    public static String addSpaces(final String expression, final ExtendedRandom random) {
        String spaced = expression;
        for (int n = StrictMath.min(10, 200 / expression.length()); n > 0;) {
            final int index = random.nextInt(spaced.length() + 1);
            final char c = index == 0 ? 0 : spaced.charAt(index - 1);
            final char nc = index == spaced.length() ? 0 : spaced.charAt(index);
            if ((safe(c) || safe(nc)) && c != '\'' && nc != '\'' && c != '"' && nc != '"') {
                spaced = spaced.substring(0, index) + " " + spaced.substring(index);
                n--;
            }
        }
        return spaced;
    }

    @Override
    public void test() {
        for (final Test test : language.getTests()) {
            try {
                test(test, prepared -> counter.scope(
                        "Testing: " + prepared,
                        () -> test.points().forEachOrdered(vars -> assertValue(
                                "original expression",
                                prepared,
                                vars,
                                test.evaluate(vars)
                        )))
                );
            } catch (final RuntimeException | AssertionError e) {
                throw new AssertionError("Error while testing " + test.parsed() + ": " + e.getMessage(), e);
            }
        }

        counter.scope("Random tests", () -> testRandom(TESTS / TestCounter.DENOMINATOR));
        stages.forEach(Runnable::run);
    }

    public static int limit(final int variables) {
        return (int) Math.floor(Math.pow(N, 1.0 / variables));
    }

    private void test(final Test test, final Consumer<Engine.Result<X>> check) {
        final Consumer<Engine.Result<X>> fullCheck = prepared -> counter.test(() -> {
            check.accept(prepared);
            test(prepared, test.unparsed());
        });
        fullCheck.accept(engine.prepare(test.parsed()));
        spoilers.forEach(spoiler -> fullCheck.accept(parse(spoiler.spoil(test.unparsed(), test.expr, random()))));
    }

    protected final Engine.Result<X> parse(final String expression) {
        return engine.parse(expression);
    }

    protected void test(final Engine.Result<X> prepared, final String unparsed) {
    }

    public void testRandom(final int n) {
        for (int i = 0; i < n; i++) {
            if (i % 100 == 0) {
                counter.format("Completed %3d out of %d%n", i, n);
            }
            final double[] vars = language.randomVars();

            final Test test = language.randomTest(i);
            final double answer = test.evaluate(vars);

            test(test, prepared -> assertValue("random expression", prepared, vars, answer));
        }
    }

    public void assertValue(final String context, final Engine.Result<X> prepared, final double[] vars, final double expected) {
        counter.test(() -> {
            final Engine.Result<Number> result = engine.evaluate(prepared, vars);
            Asserts.assertEquals("%n\tFor %s%s".formatted(context, result.context()), expected, result.value().doubleValue(), EPS);
        });
    }

    public static int mode(final String[] args, final Class<?> type, final String... modes) {
        if (args.length == 0) {
            System.err.println("ERROR: No arguments found");
        } else if (args.length > 1) {
            System.err.println("ERROR: Only one argument expected, " + args.length + " found");
        } else if (!Arrays.asList(modes).contains(args[0])) {
            System.err.println("ERROR: First argument should be one of: \"" + String.join("\", \"", modes) + "\", found: \"" + args[0] + "\"");
        } else {
            return Arrays.asList(modes).indexOf(args[0]);
        }
        System.err.println("Usage: java -ea " + type.getName() + " {" + String.join("|", modes) + "}");
        System.exit(1);
        throw new AssertionError("Return from System.exit");
    }

    public void addStage(final Runnable stage) {
        stages.add(stage);
    }

    public interface Func extends ToDoubleFunction<double[]> {
        @Override
        double applyAsDouble(double... args);
    }

    public record Test(Expr expr, String parsed, String unparsed, List<Expr> variables) {
        public Test(final Expr expr, final String parsed, final String unparsed, final List<Expr> variables) {
            this.expr = Objects.requireNonNull(expr);
            this.parsed = Objects.requireNonNull(parsed);
            this.unparsed = Objects.requireNonNull(unparsed);
            this.variables = Objects.requireNonNull(variables);
        }

        public double evaluate(final double... vars) {
            return expr.evaluate(vars);
        }

        public Stream<double[]> points() {
            final int n = limit(variables.size());
            return IntStream.range(0, N).mapToObj(i -> IntStream.iterate(i, j -> j / n)
                    .map(j -> j % n)
                    .limit(variables.size())
                    .mapToDouble(j -> j)
                    .toArray());
        }

    }

    public interface Spoiler {
        String spoil(String rendered, final Expr expr, ExtendedRandom random);
    }
}
