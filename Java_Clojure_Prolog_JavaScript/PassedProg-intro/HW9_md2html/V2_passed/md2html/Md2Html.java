package md2html;

import scannerSpace.ScannerSpace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// если добавить разметку длиной 3, все может поломаться
// :NOTE: убрать копипасту с помощью мапа.
// :NOTE: заменить на StringBuilder для хранения результата, чтобы было линейное время
// (создать два стрингбилдера: 1 - считать весь параграф, 2 - записывать изменения)

public class Md2Html {
    public static void main(String[] args) throws IOException {
        try (ScannerSpace in = new ScannerSpace(new File(args[0]), "UTF-8")) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {

                String sep = System.lineSeparator();
                List<Block> listBlocks = new ArrayList<>();

                //by BLOCKS
                while (in.hasNextSepareSpace(sep)) {
                    String curLine = in.nextSepareSpace(sep);
                    ScannerSpace inLine = new ScannerSpace(curLine);
                    String blockType = "";
                    int numHeader = 0;
                    StringBuilder strBlock = new StringBuilder();

                    //define type of BLOCK
                    if (!curLine.isEmpty()) {
                        int counter = 0;
                        StringBuilder firstWord = new StringBuilder();
                        char curCh = ' ';
                        if (inLine.hasNextChar()) {
                            curCh = inLine.nextChar();
                        }
                        if (curCh == '#') {
                            while (curCh == '#') {
                                counter++;
                                firstWord.append(curCh);
                                if (inLine.hasNextChar()) {
                                    curCh = inLine.nextChar();
                                }
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
                        while (inLine.hasNextChar()) {
                            char curCh = inLine.nextChar();
                            strBlock.append(curCh);
                        }
                        curLine = in.nextSepareSpace(sep);
                        inLine = new ScannerSpace(curLine);
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
                }

                // parse bloks
                for (Block curBlock : listBlocks) {
                    curBlock.parse();
                    writer.write(curBlock.push().toString());
                    writer.write(sep);
                }

            } catch (IOException e) {
                System.out.println("Error while writing or closing output file");
            }
        } catch (IOException e) {
            System.out.println("Error while reading or closing input file");
        }
    }
}
