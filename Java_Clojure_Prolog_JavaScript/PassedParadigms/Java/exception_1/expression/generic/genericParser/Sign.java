package expression.generic.genericParser;

public enum Sign {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    OPEN("("),
    CLOSE(")"),
    SQUARE("square"),
    AREA("area"),
    PERIMETER("perimeter"),
    NAS("_");

    private final String s;

    Sign(String s) {
        this.s = s;
    }

    public String toString() {
        return s;
    }
}