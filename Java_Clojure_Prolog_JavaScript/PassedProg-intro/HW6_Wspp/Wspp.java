import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;


public class Wspp {
    public static void main(String[] args) throws IOException {
        int i = 0;
        String[] a = new String[1];
        File fileIn = new File("D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\test.txt"); // Для отладки
        File fileOut = new File("D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\outputFile.txt");
        try(Scanner reader = new Scanner(new File(args[0]), "UTF-8")) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) { //new FileOutputStream(args[1])
                //reading elements
                while (reader.hasNextWord()) {
                    String tmpS = reader.nextWord().toLowerCase();
                    //System.err.println(tmpS);
                    if (i == a.length) {
                        a = Arrays.copyOf(a, a.length * 2);
                    }
                    a[i] = tmpS;
                    i++;
                }
                a = Arrays.copyOf(a, i);
                //System.out.println(Arrays.toString(a));

                //counting elements
                LinkedHashMap<String, IntList> map = new LinkedHashMap<>();
                for (i = 0; i < a.length; i++) {
                    String s = a[i];
                    if (map.containsKey(s)) {
                        IntList curA = map.get(s);
                        curA.insert(0, curA.get(0) + 1);
                        curA.add(i + 1);
                        curA.cut();
                        map.put(s, curA);
                    } else {
                        IntList curA = new IntList(2);
                        curA.insert(0, 1);
                        curA.insert(1, i + 1);
                        map.put(s, curA);
                    }
                }

                //writing to the output file
                for (String key : map.keySet()) {
                    String curS = key + map.get(key).showArrayBase();
                    writer.write(curS);
                    writer.write(System.lineSeparator());
                }

            } catch (FileNotFoundException e) {
                System.out.println("file to write not found" + e.getMessage());
            } catch (IOException e) {
                System.out.println("error while writing or reading" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error with opening or closing file" + e);
        }
    }
}