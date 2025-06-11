import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class WsppCountPosition {
    public static void main(String[] args) throws IOException {
        File fileIn = new File("D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\test.txt"); // Для отладки
        File fileOut = new File("D:\\my_folders\\Programming\\programms\\Prog-intro\\src\\outputFile.txt");
        try (Scanner reader = new Scanner(new File(args[0]), "UTF-8")) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) { //new FileOutputStream(args[1])
                //reading and counting elements in the same time
                Map<String, IntList> map = new LinkedHashMap<>();
                int row = 1;
                int col;
                while (reader.hasNextSepare(System.lineSeparator())) {
                    Scanner stringReader = new Scanner(reader.nextSepare(System.lineSeparator()));
                    col = 1;
                    while (stringReader.hasNextWord()) {
                        String curS = stringReader.nextWord().toLowerCase();
                        IntList curList = map.get(curS);
                        if (curList != null) {
                            curList.insert(0, curList.get(0) + 1);
                            curList.add(row);
                            curList.add(col);
                            curList.cut();
                            map.put(curS, curList);
                        } else {
                            IntList curNewList = new IntList(3);
                            curNewList.insert(0, 1);
                            curNewList.insert(1, row);
                            curNewList.insert(2, col);
                            map.put(curS, curNewList);
                        }
                        col++;
                    }
                    row++;
                }

                //sorting
                List<Map.Entry<String, IntList>> entryList = new ArrayList<>(map.entrySet());
                Collections.sort(entryList, new Comparator<Map.Entry<String, IntList>>() {
                    @Override
                    public int compare(Map.Entry<String, IntList> e1, Map.Entry<String, IntList> e2) {
                        return Integer.compare(e1.getValue().get(0), e2.getValue().get(0));
                    }
                });

                // Собираем отсортированные записи обратно в LinkedHashMap
                LinkedHashMap<String, IntList> sortedMap = new LinkedHashMap<>();
                for (Map.Entry<String, IntList> entry : entryList) {
                    sortedMap.put(entry.getKey(), entry.getValue());
                }

                //writing to the output file
                for (String key : sortedMap.keySet()) {
                    String curS = key + sortedMap.get(key).showArrayMode();
                    writer.write(curS);
                    writer.write(System.lineSeparator());
                }

            } catch (FileNotFoundException e) {
                System.out.println("file to write not found" + e.getMessage());
            } catch (IOException e) {
                System.out.println("error while writing or reading" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error by closing or opening file" + e);
        }
    }
}