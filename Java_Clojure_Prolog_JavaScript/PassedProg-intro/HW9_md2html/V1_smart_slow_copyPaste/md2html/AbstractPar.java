package md2html;

import scannerSpace.ScannerSpace;

import java.io.IOException;
import java.util.*;

public abstract class AbstractPar implements Block{
    //первый элемент - заглушка
    private final ListSymb lCode = new ListSymb(-1, 0);
    private final ListSymb lEm1 = new ListSymb(-1, 0);
    private final ListSymb lEm2 = new ListSymb(-1, 0);
    private final ListSymb lStrong1 = new ListSymb(-1, 0);
    private final ListSymb lStrong2 = new ListSymb(-1, 0);
    private final ListSymb lStrike = new ListSymb(-1, 0);
    private final ListSymb lUnderline = new ListSymb(-1, 0);

    private final List<ListSymb> lLinks = new ArrayList<>();
    private final Map<Integer, String[]> map = new LinkedHashMap<>();
    private final Map<Int>
    private int i = 0;
    private boolean onlySecond = false;
    private char curCh = ' ';
    private char nextCh = ' ';

    StringBuilder s;
    ScannerSpace scan;

    protected AbstractPar(StringBuilder s) throws IOException {
        this.s = s;
        scan = new ScannerSpace(s.toString());
        lLinks.add(lCode);
        lLinks.add(lEm1);
        lLinks.add(lEm2);
        lLinks.add(lStrong1);
        lLinks.add(lStrong2);
        lLinks.add(lStrike);
        lLinks.add(lUnderline);
        map.put(0, new String[]{"code", "`"});
        map.put(1, new String[]{"em", "*"});
        map.put(2, new String[]{"em", "_"});
        map.put(3, new String[]{"strong", "**"});
        map.put(4, new String[]{"strong", "__"});
        map.put(5, new String[]{"s", "--"});
        map.put(6, new String[]{"u", "++"});
    }

    //метод закрытия блока разметки
    public void closeMark(ListSymb closer, int beg) {
        for (ListSymb curL : lLinks) {
            if (curL != closer) {
                int i = curL.size() - 1;
                int locBeg = curL.getBeg(i);
                int locEnd = curL.getEnd(i);
                while (i > 0 && (locEnd > beg || locEnd == -1)) {
                    if (locEnd == -1 && locBeg > beg) {
                        curL.rm(i);
                    } else if (locEnd > beg && locBeg < beg) {
                        curL.rm(i);
                        curL.add(locBeg, -1);
                    }
                    i--;
                    locBeg = curL.getBeg(i);
                    locEnd = curL.getEnd(i);
                }
            }
        }
    }

    // удаляет не закрытые подмассивы
    public void delLastNotClosed() {
        for (ListSymb curL : lLinks) {
            if (curL.getEnd(curL.size() - 1) == -1) {
                curL.rm(curL.size() - 1);
            }
        }
    }

    // переопределяет разметку
    public void fill() {
        //создаем результирующий массив со всеми парами, требующими замены
        List<int[]> sumA = new ArrayList<>();
        int c = 0;
        for (ListSymb curL : lLinks) {
            for (int j = 1; j < curL.size(); j++) {
                int[] tmpA1 = new int[]{curL.getBeg(j), c, 0};
                int[] tmpA2 = new int[]{curL.getEnd(j), c, 1};
                sumA.add(tmpA1);
                sumA.add(tmpA2);
            }
            c++;
        }
        sumA.sort((a, b) -> {
            return Integer.compare(a[0], b[0]);
        });

        //меняем разметку
        int i = 0;
        int collect = 0;
        while (i < sumA.size()) {
            int[] curA = sumA.get(i);
            String[] mark = map.get(curA[1]);
            StringBuilder newMark = new StringBuilder();
            if (curA[2] == 0) {
                newMark.append("<").append(mark[0]).append(">");
            } else {
                newMark.append("</").append(mark[0]).append(">");
            }
            int stockBeg = curA[0] + collect;
            int stockEnd = curA[0] + collect + mark[1].length();
            s.replace(stockBeg, stockEnd, newMark.toString());
            collect = collect + newMark.length() - 1 - (mark[1].length() - 1);
            i++;
        }
    }

    // добавляет начало или конец разметки в массив
    public void appendToList(ListSymb l, int lenMark) {
        if (l.getEnd(l.size() - 1) != -1) {
            l.add(i, -1);
        } else {
            l.insertEnd(i);
            closeMark(l, l.getBeg(l.size() - 1));
        }
        if (lenMark == 1) {
            i++;
            curCh = nextCh;
            onlySecond = true;
        } else {
            if (i == s.length() - 3) {
                curCh = scan.nextChar();
            }
            i += 2;
            onlySecond = false;
        }
    }

    // заменяет символы, которые можно заменить сразу (без массива)
    public void replaceSpecial(String replacer) {
        s.replace(i, i + 1, replacer);
        curCh = nextCh;
        i += replacer.length();
        onlySecond = true;
    }

    @Override
    public void parse() throws IOException {
        boolean lastIter = false;
        while (scan.hasNextCharSpace() || i == s.length() - 1) {
            // считывание символа
            if (i == s.length() - 1) {
                lastIter = true;
            }
            if (!lastIter) {
                if (onlySecond) {
                    nextCh = scan.nextChar();
                } else {
                    if (scan.hasNextCharSpace()) {
                        curCh = scan.nextChar();
                    }
                    if (scan.hasNextCharSpace()) {
                        nextCh = scan.nextChar();
                    }
                }
            }

            // проверки
            if (curCh == '\\') {
                s.delete(i, i + 1);
                curCh = nextCh;
                i += 1;
                onlySecond = false;
                if (i == s.length() - 1) {
                    curCh = scan.nextChar();
                }
            } else if (curCh == '<') {
                replaceSpecial("&lt;");
            } else if (curCh == '>') {
                replaceSpecial("&gt;");
            } else if (curCh == '&') {
                replaceSpecial("&amp;");
            } else if (curCh == '`') {
                appendToList(lCode, 1);
            } else if (!lastIter && curCh == '*' && nextCh == '*') {
                appendToList(lStrong1, 2);
            } else if (!lastIter && curCh == '_' && nextCh == '_') {
                appendToList(lStrong2, 2);
            } else if (!lastIter && curCh == '-' && nextCh == '-') {
                appendToList(lStrike, 2);
            } else if (!lastIter && curCh == '+' && nextCh == '+') {
                appendToList(lUnderline, 2);
            } else if (curCh == '*') {
                appendToList(lEm1, 1);
            } else if (curCh == '_') {
                appendToList(lEm2, 1);
            } else {
                curCh = nextCh;
                i++;
                onlySecond = true;
            }
        }
        // удаляем не закрытые
        delLastNotClosed();

        // когда все уже выявлено и раскидано по массивам, нужно записать изменения
        fill();
    }
}
