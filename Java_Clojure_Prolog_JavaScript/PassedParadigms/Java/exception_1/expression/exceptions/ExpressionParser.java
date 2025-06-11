package expression.exceptions;

import expression.TripleExpression;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String expression) throws Exception {
        return (new MyParser()).parse(expression);
    }
}