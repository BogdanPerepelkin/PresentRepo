package search;

import base.MainChecker;
import base.Runner;
import base.Selector;
import base.TestCounter;

import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class BinarySearchTest {
    public static final int[] SIZES = {5, 4, 2, 1, 10, 50, 100, 200, 300};
    public static final int[] VALUES = new int[]{5, 4, 2, 1, 0, 10, 100, Integer.MAX_VALUE / 2};

    private BinarySearchTest() {
    }

    // === Base
    /* package-private */ static int base(final int c, final int x, final int[] a) {
        return IntStream.range(0, a.length).filter(i -> Integer.compare(a[i], x) != c).findFirst().orElse(a.length);
    }


    // === 36, 37

    /* package-private */ interface Sorted {
        void test(final Variant variant, final int[] a, final int[] b);
    }

    /* package-private */ static Consumer<TestCounter> sorted(final String name, final Kind kind, final Sorted solver) {
        final Sampler sampler = new Sampler(kind, false, false);
        return variant(name, variant -> {
            for (final int s : SIZES) {
                final int size = s > 3 * TestCounter.DENOMINATOR ? s / TestCounter.DENOMINATOR : s;
                for (final int max : VALUES) {
                    solver.test(variant, sampler.sample(variant, size, max), sampler.sample(variant, size, max));
                }
            }
        });
    }

    private static void uni(final int[] a, final int[] b, final BiConsumer<Integer, int[]> variant) {
        variant.accept(a.length, a);
        for (int k = 0, i = a.length - 1; k < b.length && i >= 0; k++, i--) {
            if (a[i] != b[k]) {
                a[i] = b[k];
                variant.accept(i, a);
            }
        }
    }

    private static final Sorted TEST_3637 = (variant, a, b) ->
            uni(a, b, (k, u) ->
                    ((Solver) (c, x, args) -> range(0, args.length).filter(i -> args[i] == x).findFirst().orElse(-1))
                            .test(Kind.ASC, variant, u, u.length > 100 ? 10 : Integer.MAX_VALUE));


    // === 38, 39
    private static final Sorted TEST_3839 = (variant, a, b) -> uni(
            a, b, (t, u) -> {
                final int i1 = Math.max(t - 1, 0);
                final int i2 = Math.min(t, u.length - 1);
                variant.test(u[i1] >= u[i2] ? i1 : i2, u);
            }
    );


    // === 34, 35
    private static final Sorted TEST_3435 = (tester, a, b) -> {
        for (int k = 0; k < a.length; k++) {
            tester.test(Math.min(a[k], a[(k + a.length - 1) % a.length]), a);

            final int last = a[a.length - 1];
            System.arraycopy(a, 0, a, 1, a.length - 1);
            a[0] = last;
        }
    };


    // == 32, 33
    private static final Solver TEST_3233 = (c, x, a) -> {
        final int index = range(0, a.length).filter(i -> a[i] == x || Integer.compare(x, a[i]) == c).findFirst().orElse(
                a.length);
        return index < a.length && a[index] == x ? index : -1 - index;
    };


    // === Common code

    public static final Selector SELECTOR
            = new Selector(BinarySearchTest.class)
                    .variant("Base",        Solver.variant0("", Kind.DESC, BinarySearchTest::base))
                    .variant("3233",        Solver.variant0("3233", Kind.DESC, TEST_3233))
                    .variant("3435",        sorted("3435",    Kind.ASC, TEST_3435))
                    .variant("3637",        sorted("3637",    Kind.DESC,  TEST_3637))
                    .variant("3839",        sorted("3839",    Kind.ASC, TEST_3839));

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static Consumer<TestCounter> variant(final String name, final Consumer<Variant> variant) {
        final String className = "BinarySearch" + name;
        return counter -> variant.accept(new Variant(counter, new MainChecker(Runner.packages("search").args(className))));
    }

    /* package-private */ interface Solver {
        static Consumer<TestCounter> variant0(final String name, final Kind kind, final Solver solver) {
            return variant(name, kind, true, solver);
        }

        static Consumer<TestCounter> variant1(final String name, final Kind kind, final Solver solver) {
            return variant(name, kind, false, solver);
        }

        private static Consumer<TestCounter> variant(final String name, final Kind kind, final boolean empty, final Solver solver) {
            final Sampler sampler = new Sampler(kind, true, true);
            return BinarySearchTest.variant(name, vrt -> {
                if (empty) {
                    solver.test(kind, vrt);
                }
                solver.test(kind, vrt, 0);
                for (final int s1 : SIZES) {
                    final int size = s1 > 3 * TestCounter.DENOMINATOR ? s1 / TestCounter.DENOMINATOR : s1;
                    for (final int max : VALUES) {
                        solver.test(kind, vrt, sampler.sample(vrt, size, max));
                    }
                }
            });
        }

        static int[] probes(final int[] a, final int limit) {
            return Stream.of(
                            Arrays.stream(a),
                            IntStream.range(1, a.length).map(i -> (a[i - 1] + a[i]) / 2),
                            IntStream.of(
                                    0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                                    a.length > 0 ? a[0] - 1 : -1,
                                    a.length > 0 ? a[a.length - 1] + 1 : 1
                            )
                    )
                    .flatMapToInt(Function.identity())
                    .distinct()
                    .sorted()
                    .limit(limit)
                    .toArray();
        }

        Object solve(final int c, final int x, final int... a);

        default void test(final Kind kind, final Variant variant, final int... a) {
            test(kind, variant, a, Integer.MAX_VALUE);
        }

        default void test(final Kind kind, final Variant variant, final int[] a, final int limit) {
            for (final int x : probes(a, limit)) {
                variant.test(solve(kind.d, x, a), IntStream.concat(IntStream.of(x), Arrays.stream(a)));
            }
        }
    }

    public enum Kind {
        ASC(-1), DESC(1);

        public final int d;

        Kind(final int d) {
            this.d = d;
        }
    }

    public record Variant(TestCounter counter, MainChecker checker) {
        void test(final Object expected, final IntStream ints) {
            final List<String> input = ints.mapToObj(Integer::toString).toList();
            checker.testEquals(counter, input, List.of(expected.toString()));
        }

        public void test(final Object expected, final int[] a) {
            test(expected, Arrays.stream(a));
        }
    }

    public record Sampler(Kind kind, boolean dups, boolean zero) {
        public int[] sample(final Variant variant, final int size, final int max) {
            final IntStream sorted = variant.counter.random().getRandom().ints(zero ? size : Math.max(size, 1), -max, max + 1).sorted();
            final int[] ints = (dups ? sorted : sorted.distinct()).toArray();
            if (kind == Kind.DESC) {
                final int sz = ints.length;
                for (int i = 0; i < sz / 2; i++) {
                    final int t = ints[i];
                    ints[i] = ints[sz - i - 1];
                    ints[sz - i - 1] = t;
                }
            }
            return ints;
        }
    }
}
