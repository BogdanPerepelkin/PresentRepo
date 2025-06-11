package md2html;

import java.io.IOException;

public class Header extends AbstractPar{

    int numHeader;
    public Header(StringBuilder s, int numHeader) throws IOException {
        super(s);
        this.numHeader = numHeader;
    }

    @Override
    public StringBuilder push() {
        StringBuilder beg = new StringBuilder("<h");
        return beg.append(numHeader).append(">").append(s).append("</h").append(numHeader).append(">");
    }

}