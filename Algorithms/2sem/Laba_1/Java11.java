import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Java11 {
    static Set<Integer> setY = new TreeSet<>();
    static List<int[]> request = new ArrayList<>();
    static int m;
    static int n;
    static int[] tree;

    public static void upd(int v, int l, int r, int pos, int val) {
        if (r == l + 1) {
            tree[v] += val;
            return;
        }
        int m = (l + r) / 2;
        if (pos < m) {
            upd(2 * v + 1, l, m, pos, val);
        } else {
            upd(2 * v + 2, m, r, pos, val);
        }
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        tree[v] = tree[left] + tree[right];
    }

    public static int get(int v, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        if (r <= ql || l >= qr) {
            return 0;
        }
        int m = (l + r) / 2;
        int left = get(2 * v + 1, l, m, ql, qr);
        int right = get(2 * v + 2, m, r, ql, qr);
        return left + right;
    }

    public static void main(String[] args) throws IOException {
        prepareData();

        // tree init
        tree = new int[4 * n + 1];

        // main algorithm
        int[] answerO = new int[m];
        int[] answerC = new int[m];
        for (int i = 0; i < request.size(); i++) {
            int[] req = request.get(i);
            if (req[1] == 1) {
                upd(0, 0, n, req[3], 1);
            } else {
                int tmp = get(0, 0, n, req[3], req[4] + 1);
                if (req[1] == 0) {
                    answerO[req[0]] = tmp;
                } else {
                    answerC[req[0]] = tmp;
                }
            }
        }

        // final answer
        for (int i = 0; i < m; i++) {
            System.out.println(answerC[i] - answerO[i]);
        }
//        System.out.println(Arrays.toString(answerC));
//        System.out.println(Arrays.toString(answerO));
    }

    private static void prepareData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String[] nm = br.readLine().split(" ");
        n = Integer.parseInt(nm[0]);
        m = Integer.parseInt(nm[1]);

        // reading dots (keys)
        String[] begin = br.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            int curY = Integer.parseInt(begin[i]);
            request.add(new int[]{i, 1, i, curY - 1});
        }

        // requests
        for (int i = 0; i < m; i++) {
            String[] curLine = br.readLine().split(" ");
            int x1 = Integer.parseInt(curLine[0]) - 1;
            int x2 = Integer.parseInt(curLine[1]) - 1;
            int y1 = Integer.parseInt(curLine[2]) - 1;
            int y2 = Integer.parseInt(curLine[3]) - 1;
            request.add(new int[]{i, 0, x1, y1, y2});
            request.add(new int[]{i, 2, x2, y1, y2});
        }
//        debug(request, "start");


        // sort by x, then by type (dot -> open -> close)
        request.sort(Comparator
                .comparingInt((int[] arr) -> arr[2])
                .thenComparingInt(arr -> arr[1]));

//        debug(request, "sorted");

    }

    private static void debug(List<int[]> lst, String s) {
        System.out.println(s);
        for (int i = 0; i < lst.size(); i++) {
            System.out.println(Arrays.toString(lst.get(i)));
        }
        System.out.println();
    }
}

