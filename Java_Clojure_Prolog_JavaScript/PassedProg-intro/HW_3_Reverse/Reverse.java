import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reverse {
    public static void main(String[] args) throws IOException {
        String sep = System.lineSeparator();
        List<List<Integer>> a = new ArrayList<>();
        Scanner scan = new Scanner(System.in, "UTF-8");
        while (scan.hasNextSepare(sep)) {
            String subS = scan.nextSepare(sep);
            Scanner curScan = new Scanner(subS);
            List<Integer> subA = new ArrayList<>();
            while (curScan.hasNextOptimize()) {
                subA.add(Integer.parseInt(curScan.next()));
            }
            a.add(subA);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = a.size() - 1; i >= 0; i--) {
            List<Integer> cur = a.get(i);
            if (cur.isEmpty()) {
                sb.append(System.lineSeparator());
            } else {
                for (int j = cur.size() - 1; j >= 0; j--) {
                    sb.append(cur.get(j));
                    if (j > 0) {
                        sb.append(" ");
                    }
                }
                sb.append(System.lineSeparator());
            }
        }
        System.out.print(sb);
    }
}





//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//
//public class Reverse {
//    public static void main(String[] args) throws IOException {
//        String sep = System.lineSeparator();
//        //ввод
//        int[][] a = new int[1][0];
//        int index = 0;
//        Scanner scan = new Scanner(System.in, "UTF-8"); //ЗДЕСЬ ДОЛЖЕН БЫТЬ System.in
//        while (scan.hasNextSepare(sep)) {
//            if (index == a.length) {
//                a = Arrays.copyOf(a, a.length * 2);
//            }
//            //String tmpS = scan.nextSepare(sep);
//            //System.err.println(tmpS);
//            Scanner subScan = new Scanner(scan.nextSepare(sep));
//            int[] subA = new int[1];
//            int subIndex = 0;
//            while (subScan.hasNextOptimize()) {
//                if (subIndex == subA.length) {
//                    subA = Arrays.copyOf(subA, subA.length * 2);
//                }
//                int tmpInt = Integer.parseInt(subScan.next());
//                //System.err.println(tmpInt);
//                subA[subIndex] = tmpInt;
//                subIndex++;
//            }
//            subA = Arrays.copyOfRange(subA, 0, subIndex);
//            a[index] = subA;
//            index++;
//        }
//        a = Arrays.copyOf(a, index);
//        //System.err.println(a.length);
//
//        //вывод
//        StringBuilder sb = new StringBuilder();
//        for (int i = index - 1; i >= 0; i--) {
//            if (a[i].length == 0) {
//                sb.append(System.lineSeparator());
//            } else {
//                for (int j = a[i].length - 1; j >= 0; j--) {
//                    if (j == 0) {
//                        sb.append(a[i][j]).append(System.lineSeparator());
//                    } else {
//                        sb.append(a[i][j]).append(" ");
//                    }
//                }
//            }
//        }
//        System.out.print(sb);
//    }
//}
//
