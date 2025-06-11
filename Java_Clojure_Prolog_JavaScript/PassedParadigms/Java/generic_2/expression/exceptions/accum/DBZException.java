package expression.exceptions.accum;

public class DBZException extends EvaluateException{
    public DBZException(String message) {
        super("divizion by zero" + message);
    }
}