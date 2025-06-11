package expression.generic.genericParser;


import static expression.generic.genericParser.Sign.*;
import expression.generic.genericParser.operations.*;


public class GenericParser<E> implements TripleGenericParser<E> {
    protected final NumberInterface<E> numberType;

    public GenericParser(NumberInterface<E> numberType) {
        this.numberType = numberType;
    }

    @Override
    public GenericExpression<E> parse(final String expression) {
        CharSource source;
        source = new StringSource(expression);
        return parse(source);
    }

    public GenericExpression<E> parse(final CharSource source) {
        return (GenericExpression<E>) new ParserExpression(source, this.numberType).parseExpression();
    }

    private static class ParserExpression<E> extends BaseParser {
        protected final NumberInterface<E> numberType;
        
        public ParserExpression(final CharSource source, NumberInterface<E> numberType) {
            super(source);
            this.numberType = numberType;
        }

        public GenericExpression<E> parseExpression() {
            GenericExpression<E> result = parseBinSecondRight(parseUnary());
            if (eof()) {
                return result;
            }
            throw error("End of string expected");
        }

        private GenericExpression<E> parseBinSecondRight(GenericExpression<E> left) {
            skipWhitespace();
            if (eof()) {
                return left;
            }
            Sign sign = getSign();
            return switch (sign) {
                case MULTIPLY, DIVIDE, AREA, PERIMETER -> parseBinSecondRight(parseBinFirst(left, sign));
                case ADD, SUBTRACT -> {
                    GenericExpression<E> right = parseBinFirstRight(parseUnary());
                    if (sign == ADD) {
                        yield parseBinSecondRight(new Add<E>(left, right, numberType));
                    }
                    yield parseBinSecondRight(new Subtract<E>(left, right, numberType));
                }
                default -> left;
            };
        }

        private GenericExpression<E> parseBinFirstRight(GenericExpression<E> left) {
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
                    GenericExpression<E> curLeft = parseBinFirst(left, sign);
                    yield parseBinFirstRight(curLeft);
                }
                default -> left;
            };
        }

        private GenericExpression<E> parseBinFirst(GenericExpression<E> left, Sign sign) {
            skipWhitespace();
            GenericExpression<E> right = parseUnary();
            return switch (sign) {
                case MULTIPLY -> new Multiply<E>(left, right, numberType);
                case AREA -> new Area<E>(left, right, numberType);
                case PERIMETER -> new Perimeter<E>(left, right, numberType);
                default -> new Divide<E>(left, right, numberType);
            };
        }

        private GenericExpression<E> parseBrackets(String closeBracket) {
            GenericExpression<E> result = parseBinSecondRight(parseUnary());
            skipWhitespace();
            expect(closeBracket);
            return result;
        }

        private GenericExpression<E> parseUnary() {
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

        private GenericExpression<E> parseUnaryMinus() {
            skipWhitespace();
            return new UnaryMinus<E>(parseUnary(), numberType);
        }

        private GenericExpression<E> parseConst() {
            final StringBuilder sb = new StringBuilder();
            takeNumber(sb);
            try {
                return new Const<E>(numberType.str2Num(sb.toString()));
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

        private GenericExpression<E> parseSqrt() {
            skipWhitespace();
            return new Sqrt<E>(parseUnary(), numberType);
        }

        private GenericExpression<E> parseVariable(String var) {
            return new Variable<E>(var);
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




















