package search;

import java.util.Scanner;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) {

    }

    public static int binIterative(int x, List<Integer> a) {
        int l = -1;
        int r = a.size();
        while (r - l > 1) {
            int mid = (l + r) / 2;
            if (a.get(mid) > x) {
                l = mid;
            } else {
                r = mid;
            }
        }
        return r;
    }

    public static int binRecursive(int x, int l, int r, List<Integer> a) {
        if (r - l <= 2) {
            if (a.get(l + 1) <= x) {
                return l + 1;
            }
            return r;
        }
        int mid = (l + r) / 2;
        if (a.get(mid) <= x) {
            return binRecursive(x, l, mid, a);
        }
        return binRecursive(x, mid, r, a);
    }
}
