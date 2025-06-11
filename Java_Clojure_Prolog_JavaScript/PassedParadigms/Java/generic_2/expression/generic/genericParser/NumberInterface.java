package expression.generic.genericParser;


public interface NumberInterface<T> {
    T add(T left, T right);
    T sub(T left, T right);
    T mult(T left, T right);
    T divide(T left, T right);
    T area(T left, T right);
    T perimeter(T left, T right);
    T str2Num(String s);
    T unaryMinus(T arg);
    T sqrt(T arg);
    T int2Type(Integer arg);
}