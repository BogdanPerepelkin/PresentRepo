package md2html;

import java.io.IOException;

public class Paragraph extends AbstractPar{

    public Paragraph(StringBuilder strBlock) throws IOException {
        super(strBlock);
    }

    @Override
    public StringBuilder push() {
        StringBuilder beg = new StringBuilder("<p>");
        return beg.append(s).append("</p>");
    }
}
