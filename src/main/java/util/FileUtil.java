package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    public static String readAllBytesFromFile(String filePath) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void saveData(String data){
        saveData(data,"C:/temp/data.txt");
    }

    public static void saveData(String data, String filePath){
        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            PrintWriter pw = new PrintWriter(fileWriter);
            pw.println(data);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveModel(String model, int modelNumber){
        try {
            FileWriter fileWriter = new FileWriter("C:/temp/model" + modelNumber + ".txt", false);
            PrintWriter pw = new PrintWriter(fileWriter);
            pw.println(model);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveModel(String model, String filePath){
        try {
            FileWriter fileWriter = new FileWriter(filePath, false);
            PrintWriter pw = new PrintWriter(fileWriter);
            pw.println(model);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
