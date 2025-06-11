package game;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Main {
    public static int readNum(Scanner source, int checkType) {
        int curNum = 0;
        boolean isValid = false;
        while (!isValid) {
            try {
                curNum = source.nextInt();
                if (checkType == 0) {
                    isValid = curNum == 0 || curNum == 1;
                } else {
                    isValid = curNum > 0;
                }
            } catch (InputMismatchException | NullPointerException e) {
                source.nextLine();
                System.out.println("your input is not numbers or null");
                continue;
            }
            if (!isValid) {
                System.out.println("enter exactly supported numbers");
            }
        }
        return curNum;
    }

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("You are initializing game");
            boolean wrongSettings = true;
            int mnkType = 0;
            int regulationsType = 0;
            int m = 1;
            int k = 1;
            int n = 1;
            int p = 0;
            while (wrongSettings) {
                System.out.println("What mnk game do you wanna play?");
                System.out.println("regular - 0, diamond - 1");
                mnkType = readNum(scan, 0);
                System.out.println("What first turns do you prefer?");
                System.out.println("regular - 0, debut regulations - 1");
                regulationsType = readNum(scan, 0);
                System.out.println("Enter m");
                m = readNum(scan, 1);
                if (mnkType == 0) {
                    System.out.println("Enter n");
                    n = readNum(scan, 1);
                }
                System.out.println("Enter k");
                k = readNum(scan, 1);
                if (regulationsType == 1) {
                    System.out.println("Enter p");
                    p = readNum(scan, 1);
                }
                boolean attention = mnkType == 0 && k > m && k > n
                        || mnkType == 1 && k > 2 * m - 1;
                if (attention) {
                    System.out.println("Attention! It is impossible to win");
                    System.out.println("Do you wanna remake game? Yes - 0, No - 1");
                    if (readNum(scan, 0) == 0) {
                        continue;
                    }
                }
                wrongSettings = false;
            }

            Game game = new Game(false, new HumanPlayer(), new HumanPlayer());
            int result;
            System.out.println("Game started!");
            Board board;
            if (mnkType == 0) {
                board = new MNKBoard(m, n, k, p);
            } else {
                board = new DimondBoard(m, k, p);
            }
            result = game.play(board);
            System.out.println("Result of game: " + result);

        } catch (RuntimeException e) {
            System.out.println("error while running: " + e.getMessage());
        }
    }
}