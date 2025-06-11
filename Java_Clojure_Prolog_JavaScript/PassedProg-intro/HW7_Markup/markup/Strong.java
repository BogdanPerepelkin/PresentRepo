package markup;

import java.util.List;

public class Strong extends AbstractParagraph{

    public Strong(List<Markdown> list) {
        super(list, "__", "#strong[");
    }

}