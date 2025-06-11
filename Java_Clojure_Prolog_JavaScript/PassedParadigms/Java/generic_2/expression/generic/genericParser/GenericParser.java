package expression.generic.genericParser;


import static expression.generic.genericParser.Sign.*;
import expression.generic.genericParser.operations.*;


public class GenericParser<T> implements TripleGenericParser<T> {
    protected final NumberInterface<T> numberType;

    public GenericParser(NumberInterface<T> numberType) {
        this.numberType = numberType;
    }

    @Override
    public GenericExpression<T> parse(final String expression) {
        CharSource source;
        source = new StringSource(expression);
        return parse(source);
    }

    public GenericExpression<T> parse(final CharSource source) {
        return new ParserExpression<>(source, this.numberType).parseExpression();
    }

    private static class ParserExpression<T> extends BaseParser {
        protected final NumberInterface<T> numberType;
        
        public ParserExpression(final CharSource source, NumberInterface<T> numberType) {
            super(source);
            this.numberType = numberType;
        }

        public GenericExpression<T> parseExpression() {
            GenericExpression<T> result = parseBinSecondRight(parseUnary());
            if (eof()) {
                return result;
            }
            throw error("End of string expected");
        }

        private GenericExpression<T> parseBinSecondRight(GenericExpression<T> left) {
            skipWhitespace();
            if (eof()) {
                return left;
            }
            Sign sign = getSign();
            return switch (sign) {
                case MULTIPLY, DIVIDE, AREA, PERIMETER -> parseBinSecondRight(parseBinFirst(left, sign));
                case ADD, SUBTRACT -> {
                    GenericExpression<T> right = parseBinFirstRight(parseUnary());
                    if (sign == ADD) {
                        yield parseBinSecondRight(new Add<T>(left, right, numberType));
                    }
                    yield parseBinSecondRight(new Subtract<T>(left, right, numberType));
                }
                default -> left;
            };
        }

        private GenericExpression<T> parseBinFirstRight(GenericExpression<T> left) {
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
                case MULTIPLY, DIVIDE, AREA, PERIMETER -> {
                    GenericExpression<T> curLeft = parseBinFirst(left, sign);
                    yield parseBinFirstRight(curLeft);
                }
                default -> left;
            };
        }

        private GenericExpression<T> parseBinFirst(GenericExpression<T> left, Sign sign) {
            skipWhitespace();
            GenericExpression<T> right = parseUnary();
            return switch (sign) {
                case MULTIPLY -> new Multiply<T>(left, right, numberType);
                case AREA -> new Area<T>(left, right, numberType);
                case PERIMETER -> new Perimeter<T>(left, right, numberType);
                default -> new Divide<T>(left, right, numberType);
            };
        }

        private GenericExpression<T> parseBrackets(String closeBracket) {
            GenericExpression<T> result = parseBinSecondRight(parseUnary());
            skipWhitespace();
            expect(closeBracket);
            return result;
        }

        private GenericExpression<T> parseUnary() {
            skipWhitespace();
            if (take('-')) {
                if (between('1', '9')) {
                    back();
                    return parseConst();
                }
                return parseUnaryMinus();
            } else if (isChar('x') || isChar('y') || isChar('z')) {
                return parseVariable(takeLetters());
            } else if (take('√')) {
                return parseSqrt();
            } else if (take('s')) {
                expect("qrt");
                if (between('0', '9') || isChar('x') || isChar('y') || isChar('z')) {
                    throw error("number of var after area");
                }
                return parseSqrt();
            } else if (take('(')) {
                return parseBrackets(")");
            } else if (take('{')) {
                return parseBrackets("}");
            } else if (take('[')) {
                return parseBrackets("]");
            } else {
                return parseConst();
            }
        }

        private GenericExpression<T> parseUnaryMinus() {
            skipWhitespace();
            return new UnaryMinus<T>(parseUnary(), numberType);
        }

        private GenericExpression<T> parseConst() {
            final StringBuilder sb = new StringBuilder();
            takeNumber(sb);
            try {
                return new Const<T>(numberType.str2Num(sb.toString()));
            } catch (final NumberFormatException e) {
                throw error("Invalid number " + sb);
            }
        }

        private void takeNumber(final StringBuilder sb) {
            if (take('-')) {
                sb.append('-');
            }
            if (between('0', '9')) {
                takeDigits(sb);
            } else {
                throw error("Invalid number");
            }
        }

        private GenericExpression<T> parseSqrt() {
            skipWhitespace();
            return new Sqrt<T>(parseUnary(), numberType);
        }

        private GenericExpression<T> parseVariable(String var) {
            return new Variable<T>(var);
        }

        private String takeLetters() {
            StringBuilder sb = new StringBuilder();
            while (isChar('x') || isChar('y') || isChar('z')) {
                sb.append(take());
            }
            return sb.toString();
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9') || isChar('.') || isChar(',')) {
                sb.append(take());
            }
        }
    }
}




















