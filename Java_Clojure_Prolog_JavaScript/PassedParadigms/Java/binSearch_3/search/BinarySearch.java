package search;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class BinarySearch {
    // PRED:
    // x = Integer.parseInt(args[0])
    // ∀ i > 0 a[i] = Integer.parseInt(args[i])
    // ∀ i in [0, a.size - 1] : a[i] >= a[i + 1]
    // POST:
    // print i = min([j : a[j] <= x]) if (x in a) else a.size
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            a[i - 1] = Integer.parseInt(args[i]);
        }

        if (a.length == 0) {
            // limit position
            System.out.println(0);
        } else {
            // len(a) != 0 && PRED
            int iterativeRes = binIterative(x, a);
            // len(a) != 0 && PRED
            int recursiveRes = binRecursive(x, -1, a.length, a);
            if (iterativeRes == recursiveRes) {
                System.out.println(iterativeRes);
            } else {
                System.out.println("smth goes wrong");
            }
        }
    }

    // PRED:
    // ∀ i in [0, a.size - 1] : a[i] >= a[i + 1]
    // POST:
    // print i = min([j : a[j] <= x]) if (x in a) else a.size
    public static int binIterative(int x, int[] a) {
        int l = -1;
        int r = a.length;
        // answer in (l, r]
        // r > l
        while (r - l > 1) {
            // answer in (l, r]
            // r - l > 1
            int mid = (l + r) / 2;
            // mid > l && mid < r
            // l' = l; r' = r
            if (a[mid] > x) {
                // mid < answer
                l = mid;
                // 1) l' = mid ==>> l' < answer
                // 2) r' = r >= answer
                // 3) mid > l && mid < r
                // ==>> (r' > l') && (r' - l' < r -l)
            } else {
                // mid >= answer
                r = mid;
                // 1) l' = l ==>> l' < answer
                // 2) r' = mid ==>> r' >= answer
                // 3) mid > l && mid < r
                // ==>> (r' > l') && (r' - l' < r -l)
            }
            // r' - l' < r - l ==>> while's end will be
            // l' < answer && r' >= answer ==>> answer in (l', r']
            // r' > l'
            // l = l'
            // r = r'
        }
        // answer in (l, r]
        // (r - l <= 1) && (r > l) ==>> answer = r
        return r;
    }

    // PRED:
    // len(a) != 0 && ∀ i in [0, a.size - 2] : a[i] >= a[i + 1]
    // && answer in (l, r] && a.size >= r > l >= 0
    // POST:
    // print i = min([j : a[j] <= x]) if (x in a) else len(a)
    public static int binRecursive(int x, int l, int r, int[] a) {
        // answer in (l, r]
        // r > l
        if (r - l <= 2) {
            // answer == l + 1 || answer == r
            if (a[l + 1] <= x) {
                // answer == l + 1
                return l + 1;
            }
            // answer != l + 1 ==>> answer ==r
            return r;
        }
        // r - l > 2
        int mid = (l + r) / 2;
        // mid > l && mid < r
        // l' = l; r' = r
        if (a[mid] <= x) {
            // answer in (l, mid]
            // 1) l' = l ==>> l' < answer
            // 2) r' = mid ==>> r' >= answer
            // ==>> r' > l' && answer in (l', r']
            // r' - l' < r - l ===>>> recursive will be over
            return binRecursive(x, l, mid, a);
        }
        // answer in (mid, r]
        // 1) l' = mid ==>> l' < answer
        // 2) r' = r ==>> r' >= answer
        // ==>> r' > l' && answer in (l', r']
        // r' - l' < r - l ===>>> recursive will ber over
        return binRecursive(x, mid, r, a);
    }
}
