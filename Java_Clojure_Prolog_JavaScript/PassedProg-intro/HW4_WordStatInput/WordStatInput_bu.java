import java.io.*; // * т.к. много всего из пакета io
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.InputMismatchException;



public class WordStatInput_bu {
    public static boolean checkIsWord(char curCh) {
        int type = Character.getType(curCh);
        return ((Character.isLetter(curCh))
                || (type == Character.DASH_PUNCTUATION)
                || (curCh == '\''));
    }

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                //reading elements
                String[] a = new String[1]; //массив, в который накапливаем элементы
                char[] buffer = new char[7];
                int read = reader.read(buffer);
                String remember = "-1";
                int index = 0;

                while (read != -1) {
                    int beg = -1;
                    int end = -1;
                    char curCh;
                    String subS;
                    if ((!checkIsWord(buffer[0])) && (!remember.equals("-1"))) {
                        //System.err.print("remember when space " + remember);
                        //System.err.println();
                        a[index] = remember.toLowerCase();
                        index++;
                        remember = "-1";
                    }
                    for (int j = 0; j < read; j++) {
                        if (a.length <= index) {
                            a = Arrays.copyOf(a, a.length * 2);
                        }
                        curCh = buffer[j];
                        if (checkIsWord(curCh) && (beg == -1)) {
                            beg = j;
                            //System.err.println("beg " + buffer[j]);
                        }
                        if ((beg != -1) && (!checkIsWord(curCh))) {
                            end = j - 1;
                            subS = new String(buffer, beg, end + 1 - beg);
                            //System.err.println("regular rem: " + remember);
                            //System.err.println("regular subS: " + subS);
                            if ((beg == 0) && (!remember.equals("-1"))) {
                                //System.err.println("was merge");
                                subS = remember + subS;
                                remember = "-1";
                            }
                            //System.err.println();
                            beg = -1;
                            end = -1;
                            a[index] = subS.toLowerCase();
                            index++;
                        } else if ((beg != -1) && (j == read - 1)) {
                            if (remember.equals("-1")) {
                                remember = String.valueOf(Arrays.copyOfRange(buffer, beg, read));
                            } else {
                                remember = remember + String.valueOf(Arrays.copyOfRange(buffer, beg, read));
                            }
                            //System.err.println("remember end: " + remember);
                            //System.err.println();
                        }
                    }
                    read = reader.read(buffer);
                }
                if (!remember.equals("-1")) {
                    if (a.length == index) {
                        a = Arrays.copyOf(a, a.length * 2);
                    }
                    a[index] = remember.toLowerCase();
                    index++;
                }
                a = Arrays.copyOf(a, index);

                //отладочный вывод массива a
                for (int i = 0; i < a.length; i++) {
                    //System.err.print(a[i] + " ");
                }

                //counting elements
                String[][] answ = new String[1][0]; //массив, в котором считаем частоту встреч слова
                index = 0;
                for (int i = 0; i < a.length; i++) {
                    if (index == answ.length) {
                        answ = Arrays.copyOf(answ, answ.length * 2);
                    }
                    int curC = 0;
                    boolean flag = true;
                    for (int g = 0; g < index; g++) {
                        if (answ[g][0].equals(a[i])) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int j = 0; j < a.length; j++) {
                            if (a[i].equals(a[j])) {
                                curC++;
                            }
                        }
                        String[] tempA = new String[]{a[i], Integer.toString(curC)};
                        answ[index] = tempA;
                        index++;
                    }
                }
                answ = Arrays.copyOf(answ, index);

                //writing to the output file
                for (int i = 0; i < answ.length; i++) {
                    writer.write(answ[i][0]);
                    writer.write(" ");
                    writer.write(answ[i][1]);
                    writer.write(System.lineSeparator());
                }

                //writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("file to write not found" + e.getMessage());
            } catch (IOException e) {
                System.out.println("error while writing or reading" + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println("file to read not found" + e.getMessage());
        }
    }
}