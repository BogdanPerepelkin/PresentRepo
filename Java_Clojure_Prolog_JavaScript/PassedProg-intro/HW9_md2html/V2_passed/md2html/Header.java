package md2html;

import java.io.IOException;

public class Header extends AbstractPar{
    private final int numHeader;

    public Header(StringBuilder s, int numHeader) throws IOException {
        super(s);
        this.numHeader = numHeader;
    }

    @Override
    public StringBuilder push() {
        StringBuilder beg = new StringBuilder("<h");
        return beg.append(numHeader).append(">").append(get()).append("</h").append(numHeader).append(">");
    }

}