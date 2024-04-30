package br.elissonsouza.controleordens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {    
    public static String loadTextFile(final String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        reader.close();

        return sb.toString();
    }
}
