import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ReverseMaxOct {
    public static void main(String[] args) throws IOException {
        String sep = System.lineSeparator();
        //ввод
        int[][] a = new int[1][0];
        int index = 0;
        File file = new File("D:\\my_folders\\Programming\\programms\\untitled1\\src\\test.txt"); // Для отладки
        Scanner scan = new Scanner(System.in, "UTF-8"); //ЗДЕСЬ ДОЛЖЕН БЫТЬ System.in
        while (scan.hasNextSepare(sep)) {
            if (index == a.length) {
                a = Arrays.copyOf(a, a.length * 2);
            }
            //String tmpS = scan.nextSepare(sep);
            //System.err.println(tmpS);
            Scanner subScan = new Scanner(scan.nextSepare(sep));
            int[] subA = new int[1];
            int subIndex = 0;
            while (subScan.hasNextOptimize()) {
                if (subIndex == subA.length) {
                    subA = Arrays.copyOf(subA, subA.length * 2);
                }

                //int tmpInt = Integer.parseUnsignedInt(subScan.next(), 8);
                //System.err.println(tmpInt);
                subA[subIndex] = Integer.parseUnsignedInt(subScan.next(), 8);
                subIndex++;
            }
            subA = Arrays.copyOfRange(subA, 0, subIndex);
            a[index] = subA;
            index++;
        }
        a = Arrays.copyOf(a, index);

        //вывод
        int lenRow = a.length;
        int lenCol = a[0].length;
        for (int i = 0; i < index; i++) {
            if (a[i].length > lenCol) {
                lenCol = a[i].length;
            }
        }

        // Массивы для хранения максимумов по строкам и столбцам
        int[] maxRow = new int[lenRow];
        for (int i = 0; i < lenRow; i++) {
            maxRow[i] = Integer.MIN_VALUE;
        }
        int[] maxCol = new int[lenCol];
        for (int i = 0; i < lenCol; i++) {
            maxCol[i] = Integer.MIN_VALUE;
        }

        // Первый проход: вычисляем максимумы по строкам и столбцам одновременно
        for (int i = 0; i < lenRow; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] > maxRow[i]) {
                    maxRow[i] = a[i][j];
                }
                if (a[i][j] > maxCol[j]) {
                    maxCol[j] = a[i][j];
                }
            }
        }

        // Второй проход: создаем результирующую матрицу с замененными элементами
        int[][] outA = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            outA[i] = Arrays.copyOf(a[i], a[i].length);
        }
        for (int i = 0; i < outA.length; i++) {
            for (int j = 0; j < outA[i].length; j++) {
                outA[i][j] = Math.max(maxRow[i], maxCol[j]);
            }
        }

        // Вывод результата
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < outA.length; i++) {
            if (outA[i].length == 0) {
                sb.append(System.lineSeparator());
            }
            for (int j = 0; j < outA[i].length; j++) {
                String absMax = Integer.toOctalString(outA[i][j]);
                if (j == a[i].length - 1) {
                    sb.append(absMax).append(System.lineSeparator());
                } else {
                    sb.append(absMax).append(" ");
                }
            }
        }
        System.out.print(sb);
    }
}