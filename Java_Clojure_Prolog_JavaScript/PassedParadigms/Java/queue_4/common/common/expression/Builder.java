package common.expression;

import base.Selector;
import base.TestCounter;
import base.Tester;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;

/**
 * Expression test builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Builder implements LanguageBuilder {
    public final OperationsBuilder ops;
    public List<int[]> simplifications;

    public Builder(final boolean testMulti, final List<String> variables) {
        ops = new ArithmeticBuilder(testMulti, variables);
    }

    public static Selector.Composite<Builder> selector(
            final Class<?> owner,
            final IntPredicate testMulti,
            final List<String> variables,
            final BiFunction<LanguageBuilder, TestCounter, Tester> tester,
            final String... modes
    ) {
        return Selector.composite(
                owner,
                counter -> new Builder(testMulti.test(counter.mode()), variables),
                (builder, counter) -> tester.apply(builder, counter).test(),
                modes
        );
    }

    public static Selector.Composite<Builder> selector(
            final Class<?> owner,
            final IntPredicate testMulti,
            final BiFunction<LanguageBuilder, TestCounter, Tester> tester,
            final String... modes
    ) {
        return selector(owner, testMulti, List.of("x", "y", "z"), tester, modes);
    }

    @Override
    public Variant variant() {
        return ops.variant();
    }

    @Override
    public Language language(final Dialect parsed, final Dialect unparsed) {
        final BaseVariant variant = ops.variant();
        return new Language(parsed.renamed(variant::resolve), unparsed, variant, simplifications);
    }
}
