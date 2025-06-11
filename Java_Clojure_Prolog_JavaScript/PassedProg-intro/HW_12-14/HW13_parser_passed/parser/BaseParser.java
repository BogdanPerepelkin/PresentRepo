package expression.parser;


import expression.exceptions.ParsingException;

import static expression.parser.Sign.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    private char cur = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected char take() {
        final char result = cur;
        cur = source.hasNext() ? source.next() : END;
        return result;
    }

    protected char get() {
        return cur;
    }

    protected Sign getSign() {
        return switch (cur) {
            case '+' -> {
                take();
                yield ADD;
            }
            case '-' -> {
                take();
                yield SUBTRACT;
            }
            case '*' -> {
                take();
                yield MULTIPLY;
            }
            case '/' -> {
                take();
                yield DIVIDE;
            }
            case '◣' -> {
                take();
                yield AREA;
            }
            case '▯' -> {
                take();
                yield PERIMETER;
            }
            case ')' -> CLOSE;
            case '(' -> OPEN;
            case '√' -> SQUARE;
            default -> NAS;
        };
    }

    protected void back() {
        source.back();
        source.back();
        take();
    }

    protected boolean test(final char expected) {
        return cur == expected;
    }

    protected void skipWhitespace() {
        while (Character.isWhitespace(cur)) {
            take();
        }
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected void expect(final char expected) {
        if (!take(expected)) {
            throw error("Expected '" + expected + "', found '" + cur + "'");
        }
    }

    protected boolean expect(final String value) {
        for (final char c : value.toCharArray()) {
            expect(c);
        }
        return true;
    }

    protected boolean eof() {
        return take(END);
    }

    protected ParsingException error(final String message) {
        return source.error(message + ". Found " + cur);
    }

    protected ParsingException shortError(final String message) {
        return source.error(message);
    }

    protected boolean between(final char from, final char to) {
        return from <= cur && cur <= to;
    }

    protected boolean isChar(final char ch) {
        return ch == this.cur;
    }
}
