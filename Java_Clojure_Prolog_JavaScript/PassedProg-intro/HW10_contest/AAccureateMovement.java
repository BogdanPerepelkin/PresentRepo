import java.util.Scanner;

public class AAccureateMovement {
    public static void main(String[] args) {
        try(Scanner scan = new Scanner(System.in)) {
            double a = scan.nextDouble();
            double b = scan.nextDouble();
            double n = scan.nextDouble();

            int answ = (int) (2 * Math.ceil((double) (n - b) / (double) (b - a)) + 1);

            System.out.println(answ);
        }
    }
}