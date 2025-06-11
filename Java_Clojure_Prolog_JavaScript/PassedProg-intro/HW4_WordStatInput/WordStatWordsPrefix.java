import java.io.*;
import java.util.Arrays;

public class WordStatWordsPrefix {

    public static final String REMEMBER = "";

    public static boolean checkIsWord(char curCh) {
        int type = Character.getType(curCh);
        return ((Character.isLetter(curCh))
                || (type == Character.DASH_PUNCTUATION)
                || (curCh == '\''));
    }

    public static String cut(String s) {
        if (s.length() >= 3) {
            return s.substring(0, 3);
        }
        return s;
    }

    public static String[] reverse(String[] a) {
        String[] rA = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            rA[a.length - 1 - i] = a[i];
        }
        return rA;
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "utf-8"))) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf-8"))) {
                //reading elements
                String[] a = new String[1];
                char[] buffer = new char[1024];
                int read = reader.read(buffer);
                String remember = REMEMBER;
                int index = 0;

                while (read != -1) {
                    int beg = -1;
                    int end = -1;
                    char curCh;
                    String subS;
                    if ((!checkIsWord(buffer[0])) && (!remember.equals(REMEMBER))) {
                        a[index] = cut(remember.toLowerCase());
                        index++;
                        remember = REMEMBER;
                    }
                    for (int j = 0; j < read; j++) {
                        if (a.length <= index) {
                            a = Arrays.copyOf(a, a.length * 2);
                        }
                        curCh = buffer[j];
                        if (checkIsWord(curCh) && (beg == -1)) {
                            beg = j;
                        }
                        if ((beg != -1) && (!checkIsWord(curCh))) {
                            end = j - 1;
                            subS = new String(buffer, beg, end + 1 - beg);
                            if ((beg == 0) && (!remember.equals(REMEMBER))) {
                                subS = remember + subS;
                                remember = REMEMBER;
                            }
                            beg = -1;
                            end = -1;
                            a[index] = cut(subS.toLowerCase());
                            index++;
                        } else if ((beg != -1) && (j == read - 1)) {
                            if (remember.equals(REMEMBER)) {
                                remember = String.valueOf(Arrays.copyOfRange(buffer, beg, read));
                            } else {
                                remember = remember + String.valueOf(Arrays.copyOfRange(buffer, beg, read));
                            }
                        }
                    }
                    read = reader.read(buffer);
                }
                if (!remember.equals(REMEMBER)) {
                    if (a.length == index) {
                        a = Arrays.copyOf(a, a.length * 2);
                    }
                    a[index] = cut(remember.toLowerCase());
                    index++;
                }
                a = Arrays.copyOf(a, index);
                Arrays.sort(a);
                a = reverse(a);

                //counting elements
                String[] words = new String[1];
                int[] count = new int[1];
                index = 0;
                for (String string : a) {
                    if (index == words.length) {
                        count = Arrays.copyOf(count, count.length * 2);
                        words = Arrays.copyOf(words, words.length * 2);
                    }
                    int curC = 0;
                    boolean flag = true;
                    for (int g = 0; g < index; g++) {
                        if (words[g].equals(string)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (String s : a) {
                            if (string.equals(s)) {
                                curC++;
                            }
                        }
                        words[index] = string;
                        count[index] = curC;
                        index++;
                    }
                }
                words = Arrays.copyOf(words, index);
                count = Arrays.copyOf(count, index);

                //writing to the output file
                for (int i = 0; i < words.length; i++) {
                    writer.write(words[i]);
                    writer.write(" ");
                    writer.write("" + count[i]);
                    writer.write(System.lineSeparator());
                }
            } catch (FileNotFoundException e) {
                System.out.println("file to write not found" + e.getMessage());
            } catch (IOException e) {
                System.out.println("error while writing or reading" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("file to read not found" + e.getMessage());
        }
    }
}
/*
числа как строки
линия*
 */