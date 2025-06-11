package md2html;

import java.io.IOException;

public interface Block{
    public StringBuilder push();
    public void parse() throws IOException;
}