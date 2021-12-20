package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, обеспечивающий чтение из файла
 */
public class Reader {

    /**
     * Чтение из файла
     * @param path путь к файлу, из которого хотим читать
     * @return список строк, которые в файле были разделены \n
     */
    public static List<String> readLines(String path) {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
