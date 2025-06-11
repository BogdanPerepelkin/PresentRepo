package expression.exceptions;

import expression.exceptions.accum.ParsingException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface CharSource {
    boolean hasNext();
    char next();
    void back();
    ParsingException error(String message);
}
