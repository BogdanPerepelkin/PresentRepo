package md2html;

import scanner.Scanner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Md2Html {
    public static void main(String[] args) throws IOException {

        //отладка
        args = new String[]{"D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\input.txt",
                "D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\output.txt"};

        try (Scanner in = new Scanner(new File(args[0]), "UTF-8")) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {

                String sep = System.lineSeparator();
                List<Block> listBlocks = new ArrayList<Block>();

                //by BLOCKS
                while (in.hasNextSepareSpace(sep)) {

                    String curLine = in.nextSepareSpace(sep);
                    Scanner inLine = new Scanner(curLine);
                    String blockType = "";
                    int numHeader = 0;
                    StringBuilder strBlock = new StringBuilder();

                    //define type of BLOCK
                    if (!curLine.isEmpty()) {
                        int counter = 0;
                        StringBuilder firstWord = new StringBuilder();
                        char curCh = inLine.nextChar();
                        if (curCh == '#') {
                            while (curCh == '#') {
                                counter++;
                                firstWord.append(curCh);
                                curCh = inLine.nextChar();
                            }
                            if (curCh != ' ') {
                                firstWord.append(curCh);
                                counter = 0;
                            }
                        } else {
                            firstWord.append(curCh);
                        }
                        if (counter >= 1) {
                            blockType = "h";
                            numHeader = counter;
                        } else {
                            blockType = "p";
                            strBlock.append(firstWord);
                        }
                    }

                    //run by BLOCK
                    int cLines = 0;
                    while (!curLine.isEmpty()) {
                        cLines++;
                        if (cLines != 1) {
                            strBlock.append(sep);
                        }
                        while (inLine.hasNextSpace()) {
                            char curCh = inLine.nextChar();
                            strBlock.append(curCh);
                        }
                        curLine = in.nextSepareSpace(sep);
                        inLine = new Scanner(curLine);
                    }
                    Block block;
                    if (blockType.equals("h")) {
                        block = new Header(strBlock, numHeader);
                    } else {
                        block = new Paragraph(strBlock);
                    }
                    if (!strBlock.isEmpty()) {
                        listBlocks.add(block);
                    }

                    System.err.println(strBlock);
                    System.err.println();

                }

                for (Block curBlock : listBlocks) {
                    curBlock.parse();
                    writer.write(curBlock.push().toString());
                    writer.write(sep);
                }

            } catch (IOException e) {
                System.out.println("Error while reading or closing output file");
            }
        } catch (IOException e) {
            System.out.println("Error while reading or closing input file");
        }
    }
}
/*
//проверка на блок

                    if (!curLine.isEmpty()) {
                        int counter = 0;
                        StringBuilder firstWord = new StringBuilder();
                        char curCh = inLine.nextChar();
                        if (curCh == '#') {
                            while (curCh == '#') {
                                counter++;
                                firstWord.append(curCh);
                                curCh = inLine.nextChar();
                            }
                            if (curCh != ' ') {
                                firstWord.append(curCh);
                                counter = 0;
                            }
                        } else {
                            if (inLine.hasNextSpace()) {
                                char rememberCh = curCh;
                                curCh = inLine.nextChar();
                                if (rememberCh == '*' && curCh == '*') {
                                    flagStrong = true;
                                    strongType = "**";
                                } else if (curCh == '*') {
                                    flagEm = true;
                                    emType = "*";
                                } else if (rememberCh == '_' && curCh == '_') {
                                    flagStrong = true;
                                    strongType = "__";
                                } else if (cu)
                            }

                            firstWord.append(curCh);
                        }
                        if (counter >= 1) {
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


 */