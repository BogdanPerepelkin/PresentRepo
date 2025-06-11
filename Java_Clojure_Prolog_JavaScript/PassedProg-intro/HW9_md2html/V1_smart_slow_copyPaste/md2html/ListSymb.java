package md2html;

import java.util.ArrayList;
import java.util.List;

public class ListSymb {

    List<int[]> a = new ArrayList<>();

    public ListSymb(int beg, int end) {
        this.a.add(new int[]{beg, end});
    }

    public int getBeg(int ind) {
        return a.get(ind)[0];
    }

    public int getEnd(int ind) {
        return a.get(ind)[1];
    }

    public void add(int beg, int end) {
        a.add(new int[]{beg, end});
    }

    public void insertEnd(int end) {
        int[] tmpA = a.get(a.size() - 1);
        a.remove(a.size() - 1);
        tmpA[1] = end;
        a.add(tmpA);
    }

    public void rm(int ind) {
        a.remove(ind);
    }

    public int size() {
        return a.size();
    }

}