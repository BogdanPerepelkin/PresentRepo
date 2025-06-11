package expression;

import expression.parser.GeneralInterface;

public class Sqrt extends UnCalc {
    public Sqrt(GeneralInterface obj) {
        super(obj);
    }

    @Override
    public String toString() {
        return "√(" + obj.toString() + ")";
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (int) Math.sqrt(obj.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Sqrt that) {
            return this.obj.equals(that.obj);
        }
        return false;
    }
}