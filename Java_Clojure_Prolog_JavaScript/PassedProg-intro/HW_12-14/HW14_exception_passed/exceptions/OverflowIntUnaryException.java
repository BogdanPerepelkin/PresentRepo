package expression.exceptions;

public class OverflowIntUnaryException extends EvaluateException{
    public OverflowIntUnaryException(int x, String operation) {
        super("overflow when " + operation + " " + x);
    }
}