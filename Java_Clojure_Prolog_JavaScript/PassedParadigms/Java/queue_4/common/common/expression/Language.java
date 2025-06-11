package common.expression;

import common.expression.BaseTester.Test;

import java.util.Collections;
import java.util.List;

/**
 * Expression language.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Language {
    private final Dialect parsed;
    private final Dialect unparsed;
    private final BaseVariant variant;
    private final List<Test> tests;
    private final List<int[]> simplifications;

    public Language(
            final Dialect parsed,
            final Dialect unparsed,
            final BaseVariant variant,
            final List<int[]> simplifications
    ) {
        this.parsed = parsed;
        this.unparsed = unparsed;

        this.variant = variant;
        tests = variant.getTests().stream().map(this::test).toList();
        assert simplifications == null || simplifications.isEmpty() || simplifications.size() == tests.size();
        this.simplifications = simplifications != null && simplifications.isEmpty()
                ? Collections.nCopies(tests.size(), null) : simplifications;
    }

    private Test test(final Expr expr) {
        return new Test(expr, parsed.render(expr), unparsed.render(expr), variant.getVariables());
    }

    public Test randomTest(final int size) {
        return test(variant.randomTest(size));
    }

    public double[] randomVars() {
        return variant.random().getRandom().doubles().limit(variant.getVariables().size()).toArray();
    }

    public List<Test> getTests() {
        return tests;
    }

    public List<int[]> getSimplifications() {
        return simplifications;
    }
}
