package markup;

public class Text implements Markdown{

    private final String sB;
    private final String type = "";

    public Text(String str) {
        this.sB = str;
    }

    @Override
    public void toMarkdown(StringBuilder s) {
        s.append(sB);
    }

    @Override
    public void toTypst(StringBuilder s) {
        s.append(sB);
    }

}