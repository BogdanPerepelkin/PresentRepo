import java.util.*;

public class Java6 {
    static Map<Integer, Integer> map = new HashMap<>();
    // tree on max value
    static int[] tree;

    public static void upd(int v, int l, int r, int pos, int val) {
        if (r == l + 1) {
            tree[v] = val;
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
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int[] a = new int[n];
        int cur;
        for (int i = 0; i < n; i++) {
            cur = scan.nextInt();
            a[i] = cur;
            map.put(cur, -1);
        }

        // make tree
        int treeSize = 4 * n;
        tree = new int[treeSize];

        // requests
        int q = scan.nextInt();
        int[][] request = new int[q][];
        for (int i = 0; i < q; i++) {
            int l = scan.nextInt() - 1;
            int r = scan.nextInt() - 1;
            request[i] = new int[]{l, r, i};
        }
        // System.out.println("unsorted");
        for (int i = 0; i < q; i++) {
            // System.out.println(Arrays.toString(request[i]));
        }
        // System.out.println();

        Arrays.sort(request, (tmp1, tmp2) -> Integer.compare(tmp1[1], tmp2[1]));

        // answer
        int[] answer = new int[q];

        // System.out.println(Arrays.toString(a));
        // System.out.println(q);
        // System.out.println("sorted");
        for (int i = 0; i < q; i++) {
            // System.out.println(Arrays.toString(request[i]));
        }

        // main itarations
        int j = 0;
        for (int i = 0; i < q; i++) {
            while (j != request[i][1] + 1) {
                int index = map.get(a[j]);
                if (index != -1) {
                    upd(0, 0, n, index, 0);
                }
                upd(0, 0, n, j, 1);
                map.put(a[j], j);
                j++;
            }
            answer[request[i][2]] = get(0, 0, n, request[i][0], request[i][1] + 1);
        }
        scan.close();
        // System.out.println();
        // System.out.println("tree: " + Arrays.toString(tree));
        // System.out.println("answer: " + (Arrays.toString(answer)));
        for (int i = 0; i < answer.length; i++) {
            System.out.println(answer[i]);
        }
    }
}


















