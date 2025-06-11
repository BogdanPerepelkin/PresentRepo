package markup;

import java.util.List;

public class Emphasis extends AbstractParagraph{

    public Emphasis(List<Markdown> list) {
        super(list, "*", "#emph[");
    }

}