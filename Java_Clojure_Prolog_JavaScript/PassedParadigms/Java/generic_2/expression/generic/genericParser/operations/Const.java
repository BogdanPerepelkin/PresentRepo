package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;


public class Const<T> implements GenericExpression<T> {
    private final T con;

    public Const(T con) {
        this.con = con;
    }

    public String toString() {
        return con.toString();
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return con;
    }
}