package expression.generic.genericParser;


@FunctionalInterface
public interface TripleGenericParser<E> {
    GenericExpression<E> parse(String expression);
}
