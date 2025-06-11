package md2html;

import java.io.IOException;
import java.util.*;

public abstract class AbstractPar implements Block{
    private final Map<String, Integer> mark = new HashMap<>();
    private final StringBuilder out = new StringBuilder();
    private StringBuilder in = new StringBuilder();
    private String curS = "";
    private String curPair = "";
    private int i = 0;
    private String curMark;
    private boolean isBlockEnd = false;

    private static final Map<String, String> MARK = Map.of(
            "*", "em",
            "_", "em",
            "`", "code",
            "**", "strong",
            "__", "strong",
            "--", "s",
            "++", "u"
    );

    private static final Map<String, String> SHIELD = Map.of(
            "<", "&lt;",
            ">", "&gt;",
            "&", "&amp;",
            "\\", ""
    );

    protected AbstractPar(StringBuilder s) {
        this.in = s;
        for (String el : MARK.keySet()) {
            mark.put(el, -1);
        }
    }

    public StringBuilder get() {
        return out;
    }

    public void adding(String symb) {
        int indLast = mark.get(symb);
        if (indLast == -1) {
            mark.put(symb, out.length());
            out.append(symb);
        } else {
            out.replace(indLast, indLast + symb.length(), "<" + curMark + ">");
            out.append("</").append(curMark).append(">");
            mark.put(symb, -1);
        }
    }

    @Override
    public void parse() throws IOException {
        while (i < in.length()) {
            // проверка на конец блока
            if (i == in.length() - 1) {
                isBlockEnd = true;
            }
            // чтение символа и пары символов
            curS = Character.toString(in.charAt(i));
            char nextCh = ' ';
            if (!isBlockEnd) {
                char curCh = in.charAt(i);
                nextCh = in.charAt(i + 1);
                curPair = curCh + Character.toString(nextCh);
            }

            // проверяем экранирование
            String curShield = SHIELD.get(curS);
            if (curShield != null) {
                out.append(curShield);
                // обрабатываем граничное положение - символ "\\"
                if (!isBlockEnd && curShield.isEmpty()) {
                    out.append(nextCh);
                    i += 2;
                } else {
                    i++;
                }
            } else {
                // проверяем двойную разметку
                curMark = MARK.get(curPair);
                if (!isBlockEnd && curMark != null) {
                    adding(curPair);
                    i += 2;
                } else {
                    // проверяем одинарную разметку
                    curMark = MARK.get(curS);
                    if (curMark != null) {
                        adding(curS);
                        i++;
                    } else {
                        // нет никакой разметки и экранирования
                        out.append(curS);
                        i++;
                    }
                }
            }
        }
    }
}