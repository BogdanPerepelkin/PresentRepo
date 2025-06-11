package expression.parser;

import expression.exceptions.ParsingException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StringSource implements CharSource {
    private final String data;
    private int pos = 0;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public void back() {
        pos--;
    }

    @Override
    public ParsingException error(final String message) {
        return new ParsingException(pos + ": " + message);
    }
}
