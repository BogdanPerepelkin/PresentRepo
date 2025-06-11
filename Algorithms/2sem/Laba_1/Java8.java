import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Java8 {
    static int n = 0;
    static Set<Integer> setX = new TreeSet<>();
    static Set<Integer> setY = new TreeSet<>();
    static List<Integer> uniqX = new ArrayList<>();
    static List<Integer> uniqY = new ArrayList<>();
    static int[][] request; // index, type, x, y1, y2
    static int[] min;
    static long[] amount;
    static int[] lazy; // TODO потенциально long
    static int flag = 0;

    // fill tree with addition inspired stock y_values
    private static void build(int v, int l, int r) {
        if (r == l + 1) {
            min[v] = 0;
            if (l != uniqY.size() - 1) {
                int tmp = Math.abs(uniqY.get(l + 1) - uniqY.get(l));
                amount[v] = tmp;
            } else {
                amount[v] = 0;
            }
            return;
        }
        int m = (r + l) / 2;
        build((2 * v + 1), l, m);
        build((2 * v + 2), m, r);
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        if (min[left] < min[right]) {
            min[v] = min[left];
            amount[v] = amount[left];
        } else if (min[left] > min[right]) {
            min[v] = min[right];
            amount[v] = amount[right];
        } else {
            min[v] = min[left];
            amount[v] = amount[left] + amount[right];
        }
    }

    private static void push(int v) {
        // TODO flag maybe?
        if (lazy[v] != 0) {
            apply(2 * v + 1, lazy[v]);
            apply(2 * v + 2, lazy[v]);
            lazy[v] = 0;
        }
    }

    private static void apply(int v, int val) {
        min[v] = min[v] + val;
        lazy[v] += val;
    }

    public static void upd(int v, int l, int r, int ql, int qr, int var) {
        if (ql <= l && r <= qr) {
            apply(v, var);
            return;
        }
        if (r <= ql || l >= qr) {
            return;
        }
        push(v);
        int m = (l + r) / 2;
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        upd(left, l, m, ql, qr, var);
        upd(right, m, r, ql, qr, var);
        if (min[left] < min[right]) {
            min[v] = min[left];
            amount[v] = amount[left];
        } else if (min[left] > min[right]) {
            min[v] = min[right];
            amount[v] = amount[right];
        } else {
            min[v] = min[left];
            amount[v] = amount[left] + amount[right];
        }
    }

    public static long[] get(int v, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) {
            return new long[]{min[v], amount[v]};
        }
        if (r <= ql || l >= qr) {
            return new long[]{Integer.MAX_VALUE, 0};
        }
        push(v);
        int m = (l + r) / 2;
        long[] left = get(2 * v + 1, l, m, ql, qr);
        long[] right = get(2 * v + 2, m, r, ql, qr);
        if (left[0] < right[0]) {
            return left;
        } else if(left[0] > right[0]) {
            return right;
        } else {
            return new long[]{left[0], left[1] + right[1]};
        }
    }

    public static void main(String[] args) throws IOException {
        prepareData();
        if (flag == 1) {
            System.out.println(0);
            return;
        }

        amount = new long[uniqY.size() * 4 + 1];
        min = new int[uniqY.size() * 4 + 1];
        lazy = new int[uniqY.size() * 4 + 1];
        build(0, 0, uniqY.size() - 1);

        long answer = 0;
        int j = 0;
        int g = 0;
        while (j < uniqX.size() - 1) {
            while (request[g][2] == j) {
                if (request[g][1] == 0) {
                    upd(0, 0, setY.size() - 1, request[g][3], request[g][4], +1);
                } else if (request[g][1] == 1) {
                    upd(0, 0, setY.size() - 1, request[g][3], request[g][4], -1);
                } else {
                    System.out.println("smth went wrong");
                }
                g++;
            }
            long[] partGet = get(0, 0, uniqY.size() - 1, 0, uniqY.size() - 1);
            if (partGet[0] == 0) {
                long tmp;
                if (j != uniqX.size() - 1) {
                    tmp = Math.abs(uniqX.get(j + 1) - uniqX.get(j));
                } else {
                    tmp = 0;
                }
                answer += partGet[1] * tmp;
            }
            j++;
        }

        long area = ((long) uniqY.get(uniqY.size() - 1) - uniqY.get(0))
                * ((long) uniqX.get(uniqX.size() - 1) - uniqX.get(0));

        System.out.println(area - answer);
    }

    private static void prepareData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine().trim());
        if (n == 0) {
            flag = 1;
            return;
        }

        request = new int[n * 2][];
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().trim().split(" ");
            int x1 = Integer.parseInt(parts[0]);
            int y1 = Integer.parseInt(parts[1]);
            int x2 = Integer.parseInt(parts[2]);
            int y2 = Integer.parseInt(parts[3]);
            setX.add(x1);
            setX.add(x2);
            setY.add(y1);
            setY.add(y2);
            request[2 * i] = new int[]{i, 0, x1, y1, y2};
            request[2 * i + 1] = new int[]{i, 1, x2, y1, y2};
        }

        uniqX = new ArrayList<>(setX);
        uniqY = new ArrayList<>(setY);
        Map<Integer, Integer> mapX = new HashMap<>();
        Map<Integer, Integer> mapY = new HashMap<>();

        // prepare addition
        for (int i = 0; i < uniqX.size(); i++) {
            mapX.put(uniqX.get(i), i);
        }
        for (int i = 0; i < uniqY.size(); i++) {
            mapY.put(uniqY.get(i), i);
        }

        // squeeze request
        for (int i = 0; i < request.length; i++) {
            int[] req = request[i];
            req[2] = mapX.get(req[2]);
            req[3] = mapY.get(req[3]);
            req[4] = mapY.get(req[4]);
        }

        // sort by x, then by type
        Arrays.sort(request, (a, b) -> {
            if (a[2] != b[2]) {
                return Long.compare(a[2], b[2]);
            }
            return Long.compare(a[1], b[1]);
        });
    }

    private static void debug() {
        for (int i = 0; i < request.length; i++) {
            System.out.println(Arrays.toString(request[i]));
        }
    }
}
