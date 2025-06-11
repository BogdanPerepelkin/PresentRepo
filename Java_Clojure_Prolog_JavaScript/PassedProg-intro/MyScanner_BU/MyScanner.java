package MyScanner;
import java.io.*;
import java.util.Arrays;

public class MyScanner {
    private final Reader reader;
    private char[] buffer = new char[2];
    private int read = 0;
    private int i = 0;

    public MyScanner(final InputStream inStream) throws IOException {
        reader = new InputStreamReader(inStream, "UTF-8");
        read = reader.read(buffer);
    }

    public MyScanner(String inFile) throws IOException {
        reader = new InputStreamReader(new FileInputStream(inFile), "UTF-8");
        read = reader.read(buffer);
    }

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
            if ((! Character.isWhitespace(buffer[i])) && (beg == -1)) {
                beg = i;
            }
            if ((Character.isWhitespace(buffer[i])) && (beg != -1)) {
                end = i - 1;
                break;
            }
            i++;
        }
        String curStr = remember + new String(buffer, beg, end - beg + 1);
        remember = "";
        beg = -1;
        end = -1;
        return curStr;
    }

    public boolean hasNext() throws IOException {
        char[] tempBuf = new char[0];
        int locI = i;
        while (true) {
            if (locI > read - 1) {
                int tmpL = tempBuf.length;
                tempBuf = Arrays.copyOf(tempBuf, buffer.length + tempBuf.length);
                System.arraycopy(buffer, 0, tempBuf, tmpL, buffer.length);
                read = reader.read(buffer);
                locI = 0;
                if (read < 0) {
                    if (tempBuf.length == 0) {
                        tempBuf = buffer;
                    }
                    buffer = tempBuf;
                    read = buffer.length;
                    return false;
                }
            }
            if (!Character.isWhitespace(buffer[locI])) {
                if (tempBuf.length == 0) {
                    tempBuf = buffer;
                }
                int tmpL = tempBuf.length;
                tempBuf = Arrays.copyOf(tempBuf, buffer.length + tempBuf.length);
                System.arraycopy(buffer, 0, tempBuf, tmpL, buffer.length);
                buffer = tempBuf;
                read = buffer.length;
                return true;
            }
            locI++;
        }
    }

    public String nextLine() throws IOException {
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
            if ((! System.lineSeparator().contains(Character.toString(buffer[i]))) && (beg == -1)) {
                beg = i;
            }
            if (System.lineSeparator().contains(Character.toString(buffer[i])) && (beg != -1)) {
                end = i - 1;
                break;
            }
            i++;
        }
        String curStr = remember + new String(buffer, beg, end - beg + 1);
        remember = "";
        beg = -1;
        end = -1;
        return curStr;
    }

    public boolean hasNextLine() throws IOException {
        char[] tempBuf = new char[0];
        int locI = i;
        while (true) {
            if (locI > read - 1) {
                int tmpL = tempBuf.length;
                tempBuf = Arrays.copyOf(tempBuf, buffer.length + tempBuf.length);
                System.arraycopy(buffer, 0, tempBuf, tmpL, buffer.length);
                read = reader.read(buffer);
                locI = 0;
                if (read < 0) {
                    buffer = tempBuf;
                    read = buffer.length;
                    return false;
                }
            }
            if (System.lineSeparator().contains(Character.toString(buffer[locI]))) {
                if (tempBuf.length != 0) {
                    int tmpL = tempBuf.length;
                    tempBuf = Arrays.copyOf(tempBuf, buffer.length + tempBuf.length);
                    System.arraycopy(buffer, 0, tempBuf, tmpL, buffer.length);
                    buffer = tempBuf;
                    read = buffer.length;
                }
                return true;
            }
            locI++;
        }
    }
}
