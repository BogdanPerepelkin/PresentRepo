package md2html;

import scanner.Scanner;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.ScheduledExecutorService;

public class Md2Html {
    public static void main(String[] args) throws IOException {

        //отладка
        args = new String[]{"D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\input.txt",
                "D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\output.txt"};

        try (Scanner in = new Scanner(new File(args[0]), "UTF-8")) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {

                String sep = System.lineSeparator();
                int cHeader = 1;
                String blockType = "";

                while (in.hasNextSepare(sep)) {
                    StringBuilder block = new StringBuilder();
                    StringBuilder endBlock = new StringBuilder();
                    String curLine = in.nextSepare(sep);
                    Scanner inLine = new Scanner(curLine);

                    //проверка на блок
                    if (!curLine.isEmpty()) {
                        int counter = 0;
                        String firstWord = inLine.next();
                        for (int i = 0; i < firstWord.length(); i++) {
                            if (firstWord.charAt(i) == '#') {
                                counter++;
                            }
                        }
                        if (counter == firstWord.length() && counter == cHeader) {
                            cHeader++;
                            blockType = "header";
                            block.append("<h").append(counter).append(">");
                            endBlock.append("</h").append(counter).append(">");
                        } else {
                            blockType = "paragraph";
                            block.append("<p>");
                            block.append(firstWord);
                            endBlock.append("</p>");
                        }
                    }

                    int cLines = 0;
                    while (!curLine.isEmpty()) {
                        cLines++;
                        int cTokenLine = 0;
                        if (cLines != 1) {
                            block.append(sep);
                        }
                        while (inLine.hasNextOptimize()) {
                            cTokenLine++;
                            String curT = inLine.next();
                            if (cTokenLine == 1 && blockType.equals("header")) {
                                block.append(curT);
                            } else {
                                block.append(" ").append(curT);
                            }
                        }
                        curLine = in.nextSepare(sep);
                        inLine = new Scanner(curLine);
                    }
                    if (! block.isEmpty()) {
                        block.append(endBlock);
                        writer.write(block.toString());
                        writer.write(sep);
                    }
                }

            } catch (IOException e) {
                System.out.println("Error while reading or closing output file");
            }
        } catch (IOException e) {
            System.out.println("Error while reading or closing input file");
        }
    }
}