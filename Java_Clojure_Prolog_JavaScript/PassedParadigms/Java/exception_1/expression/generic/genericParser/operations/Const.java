package expression.generic.genericParser.operations;

import expression.generic.genericParser.GenericExpression;


public class Const<E> implements GenericExpression<E> {
    private final E con;

    public Const(E con) {
        this.con = con;
    }

    public String toString() {
        return con.toString();
    }

    @Override
    public E evaluate(E x, E y, E z) {
        return con;
    }
}