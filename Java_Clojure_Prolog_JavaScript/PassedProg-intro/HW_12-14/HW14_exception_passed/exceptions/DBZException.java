package expression.exceptions;

public class DBZException extends EvaluateException{
    public DBZException(String message) {
        super("divizion by zero" + message);
    }
}