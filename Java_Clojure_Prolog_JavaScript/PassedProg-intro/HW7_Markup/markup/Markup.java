package markup;

import java.util.List;

public class Markup {
    public static void main(String[] args) {
        Paragraph paragraph = new Paragraph(List.of(
            new Strong(List.of(
                new Text("6"),
                new Text("5")
            ))
        ));
        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();
        paragraph.toMarkdown(a);
        paragraph.toTypst(b);
        System.out.println(a);
        System.out.println(b);
    }
}
        