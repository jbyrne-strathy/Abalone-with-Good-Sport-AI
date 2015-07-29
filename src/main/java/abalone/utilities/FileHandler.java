package abalone.utilities;

import abalone.GameDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

/**
 * Reads and writes required files.
 */
public class FileHandler {

    /**
     * Reads the contents of a game settings file. 
     * Adapted from "How To Read File In Java – BufferedReader", at
     * http://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
     * 
     * @param fileName The name of the file to be read
     * @return The contents of the file.
     */
    public static String read(String fileName) {

        String contents = "";

        try (BufferedReader br = new BufferedReader(new FileReader(GameDriver.PATH + "/resources/" + fileName)))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contents += sCurrentLine;
            }
        } catch (FileNotFoundException e) {
            // Ignore.  Just return empty string.
        } catch (IOException e){
            ExceptionHandler.handle(e);
        }
        return contents;
    }

    /**
     * Reads the contents of an image file.
     * Adapted from "Panel with background image : Panel « Swing JFC « Java" at
     * http://www.java2s.com/Code/Java/Swing-JFC/Panelwithbackgroundimage.htm
     * @param fileName The file, which must be stored in "resources/img/".
     * @return The image stored with the given file name.
     */
    public static Image readImage(String fileName){
        Image image = null;
        try {
            image = ImageIO.read(new File(GameDriver.PATH + "/resources/img/" + fileName));
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
        return image;
    }

    /**
     * Writes contents to a game settings file.  Doesn't overwrite an existing file.
     * Adapted from "How To Write To File In Java – BufferedWriter", at
     * http://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/
     *
     * @param fileName The name of the file to be written to.
     * @param content The string to write to the file.                 
     */
    public static void write(String fileName, String content) {
        try {
            File file = new File(GameDriver.PATH + "/resources/" + fileName);
            File path = new File(file.getParent());
            // Don't overwrite an existing file.
            if (!path.exists()){
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
    }
}
