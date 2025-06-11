package expression;

import java.util.Objects;

public class Const implements GeneralInterface {
    private final Number con;

    public Const(int con) {
        this.con = con;
    }

    public Const(long con) {
        this.con = con;
    }

    @Override
    public String toString() {
        return con.toString();
    }

    @Override
    public int evaluate(int arg) {
        return con.intValue();
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return con.longValue();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return con.intValue();
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Const that) {
            return this.con.equals(that.con);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(con);
    }
}