package expression;

import javax.swing.plaf.basic.BasicMenuBarUI;
import java.util.Objects;

public abstract class UnCalc implements GeneralInterface {
    protected final GeneralInterface obj;

    public UnCalc(GeneralInterface obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object exp) {
        return true;
    }

    @Override
    public int evaluate(int arg) {
        return 0;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return 0;
    }

    @Override
    public long evaluateL(long x, long y, long z) {
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }
}











