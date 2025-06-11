package markup;

import java.util.List;

public abstract class AbstractParagraph implements Markdown {
    private final List<Markdown> list;
    private final String typeMarkdown;
    private final String typeTypst;

    protected AbstractParagraph(List<Markdown> list, String typeMarkdown, String typeTypst) {
        this.list = list;
        this.typeMarkdown = typeMarkdown;
        this.typeTypst = typeTypst;
    }

    @Override
    public void toMarkdown(StringBuilder inS) {
        inS.append(typeMarkdown);
        for (Markdown curE : list) {
            curE.toMarkdown(inS);
        }
        inS.append(typeMarkdown);
    }

    @Override
    public void toTypst(StringBuilder inS) {
        inS.append(typeTypst);
        for (Markdown curE : list) {
            curE.toTypst(inS);
        }
        if (! typeTypst.isEmpty()) {
            inS.append("]");
        }
    }
}