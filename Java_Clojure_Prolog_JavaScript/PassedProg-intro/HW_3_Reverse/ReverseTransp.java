import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class ReverseTransp {
    public static void main(String[] args) {
        int maxLen = 0;
        Scanner scan = new Scanner(System.in);
        List<List<Integer>> a = new ArrayList<>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Scanner lineScanner = new Scanner(line);
            List<Integer> subA = new ArrayList<>();
            while (lineScanner.hasNextInt()) {
                subA.add(lineScanner.nextInt());
            }
            a.add(subA);
            maxLen = Math.max(maxLen, subA.size());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLen; i++) {
            for (List<Integer> j : a) {
                if (i < j.size() && j.get(i) % 2 != 0) {
                    sb.append(j.get(i)).append(" ");
                }
            }
            sb.append(System.lineSeparator());
        }
        System.out.print(sb);
    }
}






//import java.util.Scanner;
//import java.util.Arrays;
//
//public class ReverseTransp {
//    public static void main(String[] args) {
//        //ввод
//        int[][] a = new int[1][0];
//        int index = 0;
//        int maxLen = 0;
//        Scanner scan = new Scanner(System.in);
//        while (scan.hasNextLine()) {
//            if (index == a.length) {
//                a = Arrays.copyOf(a, a.length * 2);
//            }
//            Scanner subScan = new Scanner(scan.nextLine());
//            int[] subA = new int[1];
//            int subIndex = 0;
//            while (subScan.hasNextInt()) {
//                if (subIndex == lenOrd(subA)) {
//                    subA = Arrays.copyOf(subA, lenOrd(subA) * 2);
//                }
//                subA[subIndex] = subScan.nextInt();
//                subIndex++;
//                maxLen = Math.max(maxLen, subIndex);
//            }
//            subA = Arrays.copyOf(subA,  subIndex);
//            a[index] = subA;
//            index++;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < maxLen; i++) {
//            for (int j = 0; j < a.length; j++) {
//                if ((lenOrd(a[j]) > i) && (a[j][i] % 2 != 0)) {
//                    sb.append(a[j][i]).append(" ");
//                }
//            }
//            sb.append(System.lineSeparator());
//        }
//        System.out.print(sb);
//    }
//
//    public static int lenOrd(int[] a) {
//        if (a == null) {
//            return 0;
//        } else {
//            return a.length;
//        }
//    }
//}