package expression.generic.genericParser;


public interface NumberInterface<E> {
    E add(E left, E right);
    E sub(E left, E right);
    E mult(E left, E right);
    E divide(E left, E right);
    E area(E left, E right);
    E perimeter(E left, E right);
    E str2Num(String s);
    E unaryMinus(E arg);
    E sqrt(E arg);
    E int2Type(Integer arg);
}