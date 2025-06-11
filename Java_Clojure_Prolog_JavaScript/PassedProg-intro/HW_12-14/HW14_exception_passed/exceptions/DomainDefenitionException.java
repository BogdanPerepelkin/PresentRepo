package expression.exceptions;

public class DomainDefenitionException extends EvaluateException{
    public DomainDefenitionException(int x, String operation) {
        super("out of domain defenition for function " + operation + " when " + x);
    }
}