package expression.exceptions;

import expression.*;

import java.awt.*;

public class MyParser implements TripleParser {
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
            throw error("expected eof");
        }

        private GeneralInterface parseBinSecondRight(GeneralInterface left) {
            skipWhitespace();
            if (eof()) {
                return left;
            }
            Sign sign = getSign();
            return switch (sign) {
                case MULTIPLY, DIVIDE, AREA, PERIMETER -> parseBinSecondRight(parseBinFirst(left, sign));
                case ADD, SUBTRACT -> {
                    GeneralInterface right = parseBinFirstRight(parseUnary());
                    if (sign == Sign.ADD) {
                        yield parseBinSecondRight(new CheckedAdd(left, right));
                    }
                    yield parseBinSecondRight(new CheckedSubtract(left, right));
                }
                case CLOSE -> left;
                default -> throw error("expected signs of bin operations or closing bracket");
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
                case MULTIPLY, DIVIDE, AREA, PERIMETER -> {
                    GeneralInterface curLeft = parseBinFirst(left, sign);
                    yield parseBinFirstRight(curLeft);
                }
                case CLOSE -> left;
                default -> throw error("expected signs of bin operations or closing bracket");
            };
        }

        private GeneralInterface parseBinFirst(GeneralInterface left, Sign sign) {
            skipWhitespace();
            GeneralInterface right = parseUnary();
            return switch (sign) {
                case MULTIPLY -> new CheckedMultiply(left, right);
                case AREA -> new Area(left, right);
                case PERIMETER -> new Perimeter(left, right);
                default -> new CheckedDivide(left, right);
            };
        }

        private GeneralInterface parseBrackets(String closeBracket) {
            GeneralInterface result = parseBinSecondRight(parseUnary());
            skipWhitespace();
            expect(closeBracket);
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
            } else if (between('0', '9')) {
                return parseConst();
            }

            Sign sign = getSign();
            if (isChar(')')) {
                throw shortError("found closing bracket before argument");
            } else if (sign != Sign.NAS) {
                throw shortError("no argument before operation " + sign);
            }
            throw error("expected next argument");
        }

        private GeneralInterface parseUnaryMinus() {
            skipWhitespace();
            return new CheckedNegate(parseUnary());
        }

        private GeneralInterface parseConst() {
            final StringBuilder sb = new StringBuilder();
            takeInteger(sb);
            try {
                return new Const(Integer.parseInt(sb.toString()));
            } catch (final NumberFormatException e) {
                throw shortError("Invalid number, can't parse to int " + sb);
            }
        }

        private void takeInteger(final StringBuilder sb) {
            if (take('-')) {
                sb.append('-');
            }
            if (take('0')) {
                if (between('0', '9')) {
                    throw shortError("Invalid number, not null-number begin at 0");
                } else {
                    sb.append("0");
                }
            } else if (between('1', '9')) {
                takeDigits(sb);
            } else {
                throw error("Invalid number, haven't digits");
            }
        }

        private GeneralInterface parseSqrt() {
            skipWhitespace();
            return new CheckedSqrt(parseUnary());
        }

        private GeneralInterface parseVariable(String var) {
            char last = var.charAt(var.length() - 1);
            if (last != 'x' && last != 'y' && last != 'z') {
                throw shortError("Wrong variable, no x,y,z in the end " + var);
            }
            return new Variable(var);
        }

        private String takeLetters() {
            StringBuilder sb = new StringBuilder();
            while (isChar('x') || isChar('y') || isChar('z')) {
                sb.append(take());
            }
            return sb.toString();
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(take());
            }
            skipWhitespace();
            if (between('0', '9')) {
                throw shortError("Spaces in number");
            }
        }
    }
}




















