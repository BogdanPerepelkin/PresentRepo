import java.util.Arrays;
import java.util.Scanner;

public class JJustTheLastDigit {
    public static void main(String[] args) {
        try(Scanner scan = new Scanner(System.in)) {
            int n = scan.nextInt();
            scan.nextLine();
            int[][] a = new int[n][];
            for (int i = 0; i < n; i++) {
                String curS = scan.nextLine();
                int[] curA = new int[n];
                for (int j = 0; j < n; j++) {
                    curA[j] = Character.getNumericValue(curS.charAt(j));
                }
                a[i] = curA;
            }
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (a[i][j] != 0) {
                        for (int g = j + 1; g < n; g++) {
                            a[i][g] = (a[i][g] - a[j][g] + 10) % 10;
                        }
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    System.out.print(a[i][j]);
                }
                System.out.println();
            }
        }
    }
}