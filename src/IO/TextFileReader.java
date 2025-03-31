package IO;

import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TextFileReader {
    public List<String> lines;

    public void read(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            this.lines = lines;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    public static Path sourceDirectory() {
        try {
            Path sourceDir = new File(TextFileReader.class.getProtectionDomain()
                                     .getCodeSource()
                                     .getLocation()
                                     .toURI()).toPath().getParent();

            return sourceDir;
        } catch (Exception e) {
            System.err.println("Error getting source directory: " + e.getMessage());
            return null;
        }           
    }
    public static void main(String[] args) {
        Path filePath = sourceDirectory();
        filePath = filePath.resolve("data/SolarSystem.txt");
        TextFileReader reader = new TextFileReader();
        reader.read(filePath);
        
        DataFrame df = new DataFrame(reader.lines.toArray(new String[0]),
                                        new Class[] {Double.class, Double.class, Double.class, Double.class, Double.class,Integer.class, Integer.class, Integer.class, Integer.class});
        System.out.println(df);                            
    }
}
