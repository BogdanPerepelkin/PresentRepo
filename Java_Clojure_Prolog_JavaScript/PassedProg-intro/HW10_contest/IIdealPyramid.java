import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
public class IIdealPyramid {
    public static void main(String[] args) {
        try(Scanner scan = new Scanner(System.in)) {
            int n = scan.nextInt();
            scan.nextLine();
            int xL = Integer.MAX_VALUE;
            int xR = Integer.MIN_VALUE;
            int yL = Integer.MAX_VALUE;
            int yR = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                Scanner curS = new Scanner(scan.nextLine());
                int xI = curS.nextInt();
                int yI = curS.nextInt();
                int hI = curS.nextInt();
                xL = Math.min(xL, xI - hI);
                xR = Math.max(xR, xI + hI);
                yL = Math.min(yL, yI - hI);
                yR = Math.max(yR, yI + hI);
            }
            int hRes = (int) Math.ceil( (double) (Math.max(xR - xL, yR - yL)) / 2);
            int xRes = (int) (xL + xR) / 2;
            int yRes = (int) (yL + yR) / 2;
            System.out.print(xRes + " " + yRes + " " + hRes);
        }
    }
}