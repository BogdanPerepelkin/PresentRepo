import java.io.IOException;
import java.util.Scanner;
public class BBadTreap {
    public static void main(String[] args) {
        try(Scanner scan = new Scanner(System.in)) {
            int n = scan.nextInt();
            int cur = -710 * 25000;
            int i = 1;
            while (i <= n) {
                cur = cur + 710;
                i++;
                System.out.println(cur);
            }
        }
    }
}