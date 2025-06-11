import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Long.compress;

// Rules:
    /*
    index, type, x, y1, (y2)
     */

public class Java9 {
    static Set<Integer> setX = new TreeSet<>();
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

    public static void main(String[] args) {
        prepareData();

        // tree init
        tree = new int[4 * setY.size() + 1];

        // main algorithm
        int[] answerO = new int[m];
        int[] answerC = new int[m];
        for (int i = 0; i < request.size(); i++) {
            int[] req = request.get(i);
            if (req[1] == 1) {
                upd(0, 0, setY.size(), req[3], 1);
            } else if (req[1] == 0){
                answerO[req[0]] = get(
                        0,
                        0,
                        setY.size(),
                        req[3],
                        req[4] + 1
                );
            } else if (req[1] == 2) {
                answerC[req[0]] = get(
                        0,
                        0,
                        setY.size(),
                        req[3],
                        req[4] + 1
                );
            } else {
                System.out.println("smth went wrong");
            }
        }

        // final answer
        for (int i = 0; i < m; i++) {
            System.out.println(answerC[i] - answerO[i]);
        }
//        System.out.println(Arrays.toString(answerC));
//        System.out.println(Arrays.toString(answerO));
    }

    private static void prepareData() {
        Scanner scan = new Scanner(System.in);
        // reading dots
        n = scan.nextInt();
        for (int i = 0; i < n; i++) {
            int left = scan.nextInt();
            int right = scan.nextInt();
            setX.add(left);
            setY.add(right);
            request.add(new int[]{i, 1, left, right});
        }

        // reading areas
        m = scan.nextInt();
        for (int i = 0; i < m; i++) {
            int x1 = scan.nextInt();
            int y1 = scan.nextInt();
            int x2 = scan.nextInt();
            int y2 = scan.nextInt();
            setX.add(x1);
            setX.add(x2);
            setY.add(y1);
            setY.add(y2);
            request.add(new int[]{i, 0, x1, y1, y2});
            request.add(new int[]{i, 2, x2, y1, y2});
        }
        // debug(request, "start");

        // create map
        Map<Integer, Integer> mapX = compress(setX);
        Map<Integer, Integer> mapY = compress(setY);

        // compress coords in requests
        for (int i = 0; i < request.size(); i++) {
            int[] req = request.get(i);
            if (req[1] != 1) {
                req[2] = mapX.get(req[2]);
                req[3] = mapY.get(req[3]);
                req[4] = mapY.get(req[4]);
            } else {
                req[2] = mapX.get(req[2]);
                req[3] = mapY.get(req[3]);
            }
        }
        // debug(request, "mapped");

        // sort by x, then by type ()
        request.sort(Comparator
                .comparingInt((int[] arr) -> arr[2])
                .thenComparingInt(arr -> arr[1]));
        // debug(request, "sorted");

        scan.close();
    }

    public static Map<Integer, Integer> compress(Set<Integer> set) {
        List<Integer> callableSet = new ArrayList<>(set);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < callableSet.size(); i++) {
            map.put(callableSet.get(i), i);
        }
        return map;
    }

    private static void debug(List<int[]> lst, String s) {
        System.out.println(s);
        for(int i = 0; i < lst.size(); i++) {
            System.out.println(Arrays.toString(lst.get(i)));
        }
        System.out.println();
    }
}