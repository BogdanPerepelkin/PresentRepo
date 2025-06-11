package expression.exceptions;

import expression.TripleExpression;

public class TestOwnException {

    public static void main(String[] args) throws Exception {

        String subject = "1 perimetr3 ";
        TripleParser parser = new MyParser();
        TripleExpression test = parser.parse(subject);
//        TripleExpression test = null;
//        try {
//            test = parser.parse(subject);
//        } catch (ParsingException e) {
//            System.err.println(e.getMessage());
//        }
        System.out.println(test.toString());
        System.out.println(test.evaluate(0, 0, 0));
    }
}