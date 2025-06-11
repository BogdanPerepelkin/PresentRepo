package expression.exceptions;

public class NegativeDistanceException extends EvaluateException{
    public NegativeDistanceException(int x, int y, String operation) {
        super("overflow when " + x + " " + operation + " " + y);
    }
}