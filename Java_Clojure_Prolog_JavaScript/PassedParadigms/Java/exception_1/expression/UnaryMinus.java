package expression;


public class UnaryMinus extends UnCalc {
    public UnaryMinus(GeneralInterface obj) {
        super(obj);
    }

    @Override
    public String toString() {
        return "-(" + obj.toString() + ")";
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (-1) * obj.evaluate(x, y, z);
    }

    @Override
    public boolean equals(Object exp) {
        if (exp instanceof UnaryMinus that) {
            return this.obj.equals(that.obj);
        }
        return false;
    }
}