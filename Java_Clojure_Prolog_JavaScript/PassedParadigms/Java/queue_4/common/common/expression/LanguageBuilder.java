package common.expression;

/**
 * Expression language builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface LanguageBuilder {
    Variant variant();

    Language language(Dialect parsed, Dialect unparsed);
}
