package expression.generic.genericParser;

//заместо GeneralInterface???
public interface GenericExpression<E> {
    E evaluate(E x, E y, E z);
}