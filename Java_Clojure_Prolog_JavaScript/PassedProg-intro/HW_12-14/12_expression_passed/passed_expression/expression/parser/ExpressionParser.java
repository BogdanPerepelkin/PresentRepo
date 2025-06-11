package expression.parser;

import expression.*;

public class ExpressionParser implements TripleParser {
    private char ch = 0xffff;
    private static final char END = '\0';
    private CharSource source;

    public ExpressionParser() {
    }

    @Override
    public TripleExpression parse(String expression) {
        this.source = new StringSource(expression);
        return parse(source);
    }

    public static TripleExpression parse(final CharSource source) {
        return new ParserExpression(source).parseExpression();
    }

    private static class ParserExpression extends BaseParser {
        public ParserExpression(final CharSource source) {
            super(source);
        }

        public TripleExpression parseExpression() {
            TripleExpression result = parseBinSecondRight(parseUnary());
            if (eof()) {
                return result;
            }
            throw error("End of string expected");
        }

        private TripleExpression parseUnary() {
            skipWhitespace();
            if (take('-')) {
                return new UnaryMinus(parseUnaryMinus());
            } else if (take('x') || take('y') || take('z')) {
                return new UnaryMinus(parseVariable());
            } else if (take('(')){
                //return new UnaryMinus(parseBrackets());
            } else {
                return new UnaryMinus(parseConst());
            }
            return null;
        }

        private TripleExpression parseBinSecond(TripleExpression left, char type) {
            skipWhitespace();
            TripleExpression tmp = parseUnary();
            TripleExpression right = parseBinSecondRight(tmp);
            if (type == '+') {
                return new Add((Expression) left, (Expression) right);
            } else {
                return new Subtract((Expression) left, (Expression) right);
            }
        }

        private TripleExpression parseBinSecondRight(TripleExpression left) {
            skipWhitespace();
            if (take('*')) {
                return parseBinFirst(left, '*');
            } else if (take('/')) {
                return parseBinFirst(left, '/');
            } else if (take('+')) {
                return parseBinSecond(left, '+');
            } else if (take('-')) {
                return parseBinSecond(left, '-');
            } else {
                return parseUnary();
            }
        }

        private TripleExpression parseBinFirst(TripleExpression left, char type) {
            skipWhitespace();
            TripleExpression tmp = parseUnary();
            TripleExpression right = parseBinFirstRight(tmp);
            if (type == '*') {
                return new Multiply((Expression) left, (Expression) right);
            } else {
                return new Divide((Expression) left, (Expression) right);
            }
        }

        private TripleExpression parseBinFirstRight(TripleExpression left) {
            skipWhitespace();
            if (take('*')) {
                return parseBinFirst(left, '*');
            } else if (take('/')) {
                return parseBinFirst(left, '/');
            } else {
                return parseUnary();
            }
        }

//        private TripleExpression parseElement() {
//            skipWhitespace();
//            final TripleExpression result = parseOperation();
//            skipWhitespace();
//            return result;
//        }
//
//        private TripleExpression parseBrackets() {
//            //if (take(')')) {
//            //    return List.of();
//            //}
//            skipWhitespace();
//            TripleExpression cur = parseElement();
//            while (!take(')')) {
//
//            }
//
//        }

        private TripleExpression parseUnaryMinus() {
            skipWhitespace();
            return parseUnary();
        }

        private TripleExpression parseVariable() {
            return new Variable(Character.toString(take()));
        }

        private Const parseConst() {
            final StringBuilder sb = new StringBuilder();
            takeInteger(sb);
            try { // прям очень нравится мне try
                return new Const(Integer.parseInt(sb.toString()));
            } catch (final NumberFormatException e) {
                throw error("Invalid number " + sb);
            }
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(take());
            }
        }

        private void takeInteger(final StringBuilder sb) {
            if (take('-')) {
                sb.append('-');
            }
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                takeDigits(sb);
            } else {
                throw error("Invalid number");
            }
        }

        private void skipWhitespace() {
            while (take(' ') || take('\r') || take('\n') || take('\t')) {
                // skip
            }
        }
    }


}




















