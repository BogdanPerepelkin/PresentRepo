package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinarySearch3435 {
    // PRED:
    // ∀ i > 0 a[i] = Integer.parseInt(args[i])
    // a.size > 0
    // && ( ∃! x in [0, a.size-1] :
    //                  (∀ i in [0, x-1) a[g] < a[g+1])
    //                  && (∀ j in [x, a.size-1) a[k] < a[k+1])
    //                  )
    // POST:
    // print( a[i] : ∀ j in [0, a.size - 1] a[i] <= a[j] )
    public static void main(String[] args) {
        int[] a = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            a[i] = Integer.parseInt(args[i]);
        }

        // PRED
        int iterAnswer = iterativeMin(a);
        int recursiveAnswer;
        if (a.length == 1) {
            // limit position
            recursiveAnswer = a[0];
        } else {
            // len(a) != 1 && PRED ==>> len(a) > 1
            recursiveAnswer = recursiveMin(a, 0, a.length - 1);
        }
        if (iterAnswer == recursiveAnswer) {
            System.out.println(iterAnswer);
        } else {
            System.out.println("smth went wrong");
        }
    }

    // PRED:
    // len(a) > 0
    // && ( ∃! x,y in [0, a.size-1] :
    //                  (∀ i,j in [x, y] a[i] = a[j])
    //                  && (∀ g in [0, x-1) a[g] <= a[g+1])
    //                  && (∀ k in [y, a.size-1) a[k] <= a[k+1])
    //                  )
    //      || ∀ i in [0, a.size - 1] : a[i] <= a[i + 1] )
    // POST:
    // print( a[i] : ∀ j in [0, a.size - 1] a[i] <= a[j] )
    public static int iterativeMin(int[] a) {
        int l = 0;
        int r = a.length - 1;
        // l = 0 && r = len(a) - 1 && len(a) != 0 ==>> r > l
        // answer in [l, r]
        while (r - l > 1) {
            // answer in [l, r]
            // r - l > 1
            int mid = (l + r) / 2;
            // mid < r && mid > l
            // l' = l; r' = r
            if (a[mid] < a[r]) {
                // answer not in (mid, r] && PRED ==>> answer in [l, mid]
                r = mid;
                // r > l
            } else {
                // answer not in [l, mid) && PRED ==>> answer in [mid, r]
                l = mid;
                // r > l
            }
            // answer in [l, r]
            // r > l
        }
        // answer in [l, r] && r - l <= 1 && r > l ==>> r - l = 1
        if (a[l] < a[r]) {
            // answer == l
            return a[l];
        } else {
            // answer == r
            return a[r];
        }
    }

    // PRED:
    // answer in [l, r] && 0 <= l < r < a.size
    // && len(a) > 1
    // && ( ∃! x,y in [0, a.size-1] :
    //                  (∀ i,j in [x, y] a[i] = a[j])
    //                  && (∀ g in [0, x-1) a[g] <= a[g+1])
    //                  && (∀ k in [y, a.size-1) a[k] <= a[k+1])
    //                  )
    //      || ∀ i in [0, a.size - 1] : a[i] <= a[i + 1] )
    // POST:
    // print( a[i] : ∀ j in [0, a.size - 1] a[i] <= a[j] )
    public static int recursiveMin(int[] a, int l, int r) {
        // answer in [l, r]
        // r > l
        if (r - l <= 1) {
            // r - l <= 1 && r > l ==>> answer == l || answer == r
            if (a[l] < a[r]) {
                // answer == l
                return a[l];
            } else {
                // answer == r
                return a[r];
            }
        }
        int mid = (l + r) / 2;
        // mid < r && mid > l
        if (a[mid] < a[r]) {
            // answer in [l, mid]
            // l' = l && r' = mid ==>> (r' - l' < r - l) && (r' > l')
            return recursiveMin(a, l, mid);
        } else {
            // answer in [mid, r]
            // l' = mid && r' = r ==>> (r' - l' < r -l) && (r' > l')
            return recursiveMin(a, mid, r);
        }
    }
}