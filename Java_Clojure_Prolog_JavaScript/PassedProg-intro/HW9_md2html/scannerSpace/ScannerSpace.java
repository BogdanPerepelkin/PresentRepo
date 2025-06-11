package scannerSpace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Особенности: сепаратор не должен содержать пробелы, иначе программа может работать некорректно

public class ScannerSpace implements AutoCloseable {
    private final Reader reader;
    private final int stockBufferLen = 1024;
    private char[] buffer = new char[stockBufferLen];
    private List<Character> tempBuffer = new ArrayList<>();
    private int read = 0;
    private int i = 0;
    private int curI;
    private char curCh;
    private boolean wasSpace;

    //чтение потока
    public ScannerSpace(final InputStream inStream, String encoding) throws IOException {
        reader = new InputStreamReader(inStream, encoding);
        read = reader.read(buffer);
    }

    //чтение файла
    public ScannerSpace(final File inFile, String encoding) throws IOException {
        reader = new InputStreamReader(new FileInputStream(inFile), encoding);
        read = reader.read(buffer);
    }

    //чтение строки
    public ScannerSpace(final String str) throws IOException {
        reader = new StringReader(str);
        read = reader.read(buffer);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    //переенос символов из временного буфера в глобальный
    public void postponeTempToReg() {
        char[] tempChBuf = new char[tempBuffer.size()];
        for (int j = 0; j < tempChBuf.length; j++) {
            tempChBuf[j] = tempBuffer.get(j);
        }
        buffer = tempChBuf;
        read = tempChBuf.length;
        tempBuffer.clear();
        i = 0;
    }

    public static boolean checkIsWord(char curCh) {
        int type = Character.getType(curCh);
        return ((Character.isLetter(curCh))
                || (type == Character.DASH_PUNCTUATION)
                || (curCh == '\''));
    }

    //дозаполнение временного буфера
    public void fillTemp(String sep) {
        while (curI < read) {
            curCh = buffer[curI];
            if ((!wasSpace)
                    || (sep.contains(Character.toString(curCh)))
                    || (!Character.isWhitespace(curCh))) {
                tempBuffer.add(curCh);
                if (Character.isWhitespace(curCh) && (!sep.contains(Character.toString(curCh)))) {
                    wasSpace = true;
                } else {
                    wasSpace = false;
                }
            }
            curI++;
        }
    }

    public void fillTempSpace(String sep) {
        while (curI < read) {
            curCh = buffer[curI];
            tempBuffer.add(curCh);
            curI++;
        }
    }

    // проверка на существование символа без скипа пробелов
    public boolean hasNextChar() throws IOException {
        if (i >= read) {
            read = reader.read(buffer);
            i = 0;
            if (read > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    // считывание символа
    public char nextChar() {
        i++;
        return buffer[i - 1];
    }

    //проверка на то, что либо есть сепаратор, либо в конце ввода есть что-то отличное от сепаратора
    public boolean hasNextSepare(String sep) throws IOException {
        char[] sepA = sep.toCharArray();
        int sepInd = 0;
        boolean wasSepare = false;
        wasSpace = false;
        curI = i;
        while (true) {
            if (curI >= read) {
                read = reader.read(buffer);
                curI = 0;
                if (read < 0) {
                    postponeTempToReg();
                    return false;
                }
            }
            curCh = buffer[curI];
            if ((!wasSpace) || (sep.contains(Character.toString(curCh))) || (!Character.isWhitespace(curCh))) {
                tempBuffer.add(curCh);
                if (Character.isWhitespace(curCh) && (!sep.contains(Character.toString(curCh)))) {
                    wasSpace = true;
                } else {
                    wasSpace = false;
                }
            }
            if (sepA[sepInd] == curCh) {
                sepInd++;
                if (sepInd == sepA.length) {
                    curI++;
                    fillTemp(sep);
                    postponeTempToReg();
                    return true;
                }
            } else {
                curI++;
                sepInd = 0;
                fillTemp(sep);
                postponeTempToReg();
                return true;
            }
            curI++;
        }
    }

    public boolean hasNextSepareSpace(String sep) throws IOException {
        char[] sepA = sep.toCharArray();
        int sepInd = 0;
        boolean wasSepare = false;
        curI = i;
        while (true) {
            if (curI >= read) {
                read = reader.read(buffer);
                curI = 0;
                if (read < 0) {
                    postponeTempToReg();
                    return false;
                }
            }
            curCh = buffer[curI];
            tempBuffer.add(curCh);
            if (sepA[sepInd] == curCh) {
                sepInd++;
                if (sepInd == sepA.length) {
                    curI++;
                    fillTempSpace(sep);
                    postponeTempToReg();
                    return true;
                }
            } else {
                curI++;
                sepInd = 0;
                fillTempSpace(sep);
                postponeTempToReg();
                return true;
            }
            curI++;
        }
    }

    //вернет строчку, которая идет до следующего сепаратора, либо до конца ввода, если в конце не сепаратор
    public String nextSepare(String sep) throws IOException {
        StringBuilder sepS = new StringBuilder();
        StringBuilder tempS = new StringBuilder();
        char[] sepA = sep.toCharArray();
        int sepInd = 0;
        wasSpace = false;
        while (true) {
            if (i >= read) {
                read = reader.read(buffer);
                i = 0;
                if (read < 0) {
                    return tempS.toString();
                }
            }
            curCh = buffer[i];
            if (curCh == sepA[sepInd]) {
                sepS.append(curCh);
                sepInd++;
                if (sep.length() == sepInd) {
                    i++;
                    return tempS.toString();
                }
            } else {
                sepInd = 0;
                tempS.append(sepS);
                sepS = new StringBuilder();
                if ((!wasSpace) || (sep.contains(Character.toString(curCh))) || (!Character.isWhitespace(curCh))) {
                    tempS.append(curCh);
                    if (Character.isWhitespace(curCh) && (!sep.contains(Character.toString(curCh)))) {
                        wasSpace = true;
                    } else {
                        wasSpace = false;
                    }
                }
            }
            i++;
        }
    }

    public String nextSepareSpace(String sep) throws IOException {
        StringBuilder sepS = new StringBuilder();
        StringBuilder tempS = new StringBuilder();
        char[] sepA = sep.toCharArray();
        int sepInd = 0;
        while (true) {
            if (i >= read) {
                read = reader.read(buffer);
                i = 0;
                if (read < 0) {
                    return tempS.toString();
                }
            }
            curCh = buffer[i];
            if (curCh == sepA[sepInd]) {
                sepS.append(curCh);
                sepInd++;
                if (sep.length() == sepInd) {
                    i++;
                    return tempS.toString();
                }
            } else {
                sepInd = 0;
                tempS.append(sepS);
                sepS = new StringBuilder();
                tempS.append(curCh);
            }
            i++;
        }
    }

    //hasNext как в обычном сканере, но скипаем лишние пробелы
    public boolean hasNextOptimize() throws IOException { //скипаем лишние пробелы
        wasSpace = false;
        curI = i;
        while (true) {
            if (curI >= read) {
                read = reader.read(buffer);
                curI = 0;
                if (read <= 0) {
                    postponeTempToReg();
                    return false;
                }
            }
            curCh = buffer[curI];
            if (Character.isWhitespace(curCh)) {
                if ((!wasSpace) || (System.lineSeparator().contains(Character.toString(curCh)))) {
                    tempBuffer.add(buffer[curI]);
                }
                curI++;
                wasSpace = true;
            } else {
                wasSpace = false;
                fillTemp(System.lineSeparator());
                postponeTempToReg();
                return true;
            }
        }
    }

    public boolean hasNextSpace() throws IOException {
        curI = i;
        while (true) {
            if (curI >= read) {
                read = reader.read(buffer);
                curI = 0;
                if (read <= 0) {
                    postponeTempToReg();
                    return false;
                }
            }
            curCh = buffer[curI];
            if (Character.isWhitespace(curCh)) {
                tempBuffer.add(buffer[curI]);
                curI++;
            } else {
                fillTemp(System.lineSeparator());
                postponeTempToReg();
                return true;
            }
        }
    }

    //next как в обычном сканере
    public String next() throws IOException {
        String remember = "";
        int beg = -1;
        int end = -1;
        while (true) {
            if (i > read - 1) {
                if (beg != -1) {
                    remember = remember + new String(buffer, beg, read - beg);
                }
                read = reader.read(buffer);
                i = 0;
                if (beg != -1) {
                    beg = 0;
                } else {
                    beg = -1;
                }
                end = -1;
                if (read < 0) {
                    break;
                }
            }
            if ((!Character.isWhitespace(buffer[i])) && (beg == -1)) {
                beg = i;
            }
            if ((Character.isWhitespace(buffer[i])) && (beg != -1)) {
                end = i - 1;
                break;
            }
            i++;
        }
        String curStr = remember + new String(buffer, beg, end - beg + 1);
        return curStr;
    }

    public boolean hasNextWord() throws IOException { //скипаем лишние пробелы
        wasSpace = false;
        curI = i;
        while (true) {
            if (curI >= read) {
                read = reader.read(buffer);
                curI = 0;
                if (read < 0) {
                    postponeTempToReg();
                    return false;
                }
            }
            curCh = buffer[curI];
            if (Character.isWhitespace(curCh)) {
                if ((!wasSpace) || (System.lineSeparator().contains(Character.toString(curCh)))) {
                    tempBuffer.add(buffer[curI]);
                }
                curI++;
                wasSpace = true;
            } else if (checkIsWord(curCh)) {
                wasSpace = false;
                fillTemp(System.lineSeparator());
                postponeTempToReg();
                return true;
            } else {
                tempBuffer.add(buffer[curI]);
                wasSpace = false;
                curI++;
            }
        }
    }

    public String nextWord() throws IOException {
        String remember = "";
        int beg = -1;
        int end = -1;
        while (true) {
            if (i > read - 1) {
                if (beg != -1) {
                    remember = remember + new String(buffer, beg, read - beg);
                }
                read = reader.read(buffer);
                i = 0;
                if (beg != -1) {
                    beg = 0;
                } else {
                    beg = -1;
                }
                end = -1;
                if (read < 0) {
                    break;
                }
            }
            if ((checkIsWord(buffer[i])) && (beg == -1)) {
                beg = i;
            }
            if ((!checkIsWord(buffer[i])) && (beg != -1)) {
                end = i - 1;
                break;
            }
            i++;
        }
        String curStr = remember + new String(buffer, beg, end - beg + 1);
        return curStr;
    }
}