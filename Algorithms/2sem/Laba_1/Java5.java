import java.util.*;

public class Java5 {
    static int MOD = 1_000_000_007;
    static long[] length, amount;
    static int n;

    public static void upd(int v, int l, int r, int pos) {
        if (r == l + 1) {
            long[] cur = get(0, 0, n, 0, pos);
            length[v] = cur[0] + 1;
            amount[v] = (amount[v] + Math.max(cur[1], 1)) % MOD;
            return;
        }
        int m = (l + r) / 2;
        if (pos < m) {
            upd(2 * v + 1, l, m, pos);
        } else {
            upd(2 * v + 2, m, r, pos);
        }
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        if (length[left] > length[right]) {
            length[v] = length[left];
            amount[v] = amount[left];
        } else if (length[right] > length[left]) {
            length[v] = length[right];
            amount[v] = amount[right];
        } else {
            length[v] = length[left];
            amount[v] = (amount[left] + amount[right]) % MOD;
        }
    }

    public static long[] get(int v, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) {
            return new long[]{length[v], amount[v]};
        }
        if (r <= ql || l >= qr) {
            return new long[]{0, 0};
        }
        int m = (l + r) / 2;
        long[] left = get(2 * v + 1, l, m, ql, qr);
        long[] right = get(2 * v + 2, m, r, ql, qr);
        if (left[0] > right[0]) {
            return left;
        } else if (right[0] > left[0]) {
            return right;
        }
        return new long[]{left[0], (left[1] + right[1])};
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        int[] inp = new int[n];
        for (int i = 0; i < n; i++) {
            inp[i] = scanner.nextInt();
        }
        scanner.close();

        int[] sor = Arrays.copyOf(inp, n);
        Arrays.sort(sor);
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indexMap.put(sor[i], i);
        }

        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = indexMap.get(inp[i]);
        }

        int treeSize = (1 << (int) (Math.ceil(Math.log(n) / Math.log(2)) + 1)) - 1;
        length = new long[treeSize];
        amount = new long[treeSize];

        for (int i = 0; i < n; i++) {
            upd(0, 0, n, a[i]);
        }

        System.out.println(amount[0] % MOD);
    }
}
