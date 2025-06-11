import java.util.Scanner;

public class ReverseBu {
    public static void main(String[] args) {
        int[][] a = new int[1][0];
        int index = 0;
        Scanner scan = new Scanner(System.in);

        while (scan.hasNextLine()) {
            String str = scan.nextLine();
            int[] subA;
            if (str.isEmpty()) {
                subA = new int[0];
            } else {
                subA = parse(str);
            }
            a[index] = subA;
            index++;
            if (index >= a.length) {
                a = copyBin(a);
            }
        }

        for (int i = index - 1; i >= 0; i--) {
            if (a[i].length == 0) {
                System.out.println();
            } else {
                for (int j = a[i].length - 1; j >= 0; j--) {
                    if (j == 0) {
                        System.out.println(a[i][j]);
                    } else {
                        System.out.printf(a[i][j] + " ");
                    }
                }
            }
        }
    }
    public static int[][] copyBin(int[][] a) { //двойной массив
        int[][] b = new int[a.length * 2][0];
        for (int i = 0; i < a.length; i++) {
            int[] c = new int[a[i].length];
            for (int j = 0; j < a[i].length; j++) {
                c[j] = a[i][j];
            }
            b[i] = c;
        }
        return b;
    }
    public static int[] copyOrdMult(int[] a) { //ординарный массив
        int[] b = new int[a.length * 2];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        return b;
    }
    public static int[] copyOrd(int[] a, int beg, int end) { //ординарный массив
        int[] b = new int[end];
        for (int i = beg; i < end; i++) {
            b[i] = a[i];
        }
        return b;
    }
    public static int[] parse(String str) {
        int[] a = new int[1];
        int index = 0;
        int beg = -1;
        int end = -1;
        String subS;
        char curCh;
        for (int i = 0; i < str.length(); i++) {
            curCh = str.charAt(i);
            if (!(Character.isWhitespace(curCh)) && (beg == -1)) {
                beg = i;
            }
            if ((beg != -1) && (Character.isWhitespace(curCh)
                    || (i == str.length() - 1))) {
                if (Character.isWhitespace(curCh)) {
                    end = i - 1;
                } else {
                    end = str.length() - 1;
                }
                subS = str.substring(beg, end + 1);
                int n = Integer.parseInt(subS);
                a[index] = n;
                index++;
                beg = -1;
                end = -1;
            }
            if (index == a.length) {
                a = copyOrdMult(a);
            }
        }
        return copyOrd(a,0, index);
    }
}


