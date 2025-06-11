package markup;

import java.util.List;

public class Strikeout extends AbstractParagraph{

    public Strikeout(List<Markdown> list) {
        super(list, "~", "#strike[");
    }

}