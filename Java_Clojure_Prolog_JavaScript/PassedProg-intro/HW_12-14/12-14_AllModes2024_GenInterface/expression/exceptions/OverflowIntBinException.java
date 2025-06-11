package expression.exceptions;

public class OverflowIntBinException extends EvaluateException{
    public OverflowIntBinException(int x, int y, String operation) {
        super("overflow when " + x + " " + operation + " " + y);
    }
}