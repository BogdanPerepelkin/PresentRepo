package expression.parser;

import expression.*;

import static expression.parser.Sign.*;

// NOTE:
// хардкод (хреновый парсинг)
// enum
// ген интерфейс

public class ExpressionParser implements TripleParser {
    // :NOTE: ? - бесполезный конструктор

    @Override
    public TripleExpression parse(final String expression) {
        CharSource source;
        source = new StringSource(expression);
        return parse(source);
    }

    public static GeneralInterface parse(final CharSource source) {
        return new ParserExpression(source).parseExpression();
    }

    private static class ParserExpression extends BaseParser {
        public ParserExpression(final CharSource source) {
            super(source);
        }

        public GeneralInterface parseExpression() {
            GeneralInterface result = parseBinSecondRight(parseUnary());
            if (eof()) {
                return result;
            }
            throw error("End of string expected");
        }

        private GeneralInterface parseBinSecondRight(GeneralInterface left) {
            skipWhitespace();
            if (eof()) {
                return left;
            }
            Sign sign = getSign();
            return switch (sign) {
                // :NOTE: cast ?!
                case MULTIPLY, DIVIDE -> parseBinSecondRight(parseBinFirst(left, sign));
                case ADD, SUBTRACT -> {
                    GeneralInterface right = parseBinFirstRight(parseUnary());
                    if (sign == ADD) {
                        yield parseBinSecondRight(new Add(left, right));
                    }
                    yield parseBinSecondRight(new Subtract(left, right));
                }
                default -> left;
            };
        }

        private GeneralInterface parseBinFirstRight(GeneralInterface left) {
            skipWhitespace();
            if (eof()) {
                return left;
            }
            Sign sign = getSign();
            return switch (sign) {
                case ADD, SUBTRACT -> {
                    back();
                    yield left;
                }
                case MULTIPLY, DIVIDE -> {
                    GeneralInterface curLeft = parseBinFirst(left, sign);
                    yield parseBinFirstRight(curLeft);
                }
                default -> left;
            };
        }

        private GeneralInterface parseBinFirst(GeneralInterface left, Sign sign) {
            skipWhitespace();
            GeneralInterface right = parseUnary();
            return switch (sign) {
                case MULTIPLY -> new Multiply(left, right);
                default -> new Divide(left, right);
            };
        }

        private GeneralInterface parseBrackets() {
            GeneralInterface result = parseBinSecondRight(parseUnary());
            skipWhitespace();
            expect(')');
            return result;
        }

        private GeneralInterface parseUnary() {
            skipWhitespace();
            if (take('-')) {
                if (between('1', '9')) {
                    back();
                    return parseConst();
                }
                return parseUnaryMinus();
            } else if (between('A', 'Z') || between('a', 'z')) {
                return parseVariable(takeLetters());
            } else if (take('√')) {
                return parseSqrt();
            } else if (take('(')) {
                return parseBrackets();
            } else {
                return parseConst();
            }
        }

        private GeneralInterface parseUnaryMinus() {
            skipWhitespace();
            return new UnaryMinus(parseUnary());
        }

        private GeneralInterface parseConst() {
            final StringBuilder sb = new StringBuilder();
            takeInteger(sb);
            try {
                // :NOTE: hardcoded
                return new Const(Integer.parseInt(sb.toString()));
            } catch (final NumberFormatException e) {
                throw error("Invalid number " + sb);
            }
        }

        private void takeInteger(final StringBuilder sb) {
            if (take('-')) {
                sb.append('-');
            }
            if (between('0', '9')) {
                takeDigits(sb);
            } else {
                throw error("Invalid number");
            }
        }

        private GeneralInterface parseSqrt() {
            skipWhitespace();
            return new Sqrt(parseUnary());
        }

        private GeneralInterface parseVariable(String var) {
            return new Variable(var);
        }

        private String takeLetters() {
            StringBuilder sb = new StringBuilder();
            while (between('A', 'Z') || between('a', 'z')) {
                sb.append(take());
            }
            return sb.toString();
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(take());
            }
        }
    }
}




















