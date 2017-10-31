package textManager;

import clases.TSB_OAHashtable;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFile {

    private final File file;
    private final TSB_OAHashtable<Integer, Word> words;

    /**
     * Read a file and load the words in a hashtable
     * @param file to be read
     */
    public TextFile(File file) {
        this.file = new File(file.getPath());
        this.words = new TSB_OAHashtable<>(101);
    }


    public String getPath() {
        return file.getAbsolutePath();
    }

    public void processFile() {
        String pattern = wordPattern();
        Pattern matchPattern = Pattern.compile(pattern);
        Matcher matcher;

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String textLine = bufferedReader.readLine();
            while (textLine != null) {
                String[] words_list = textLine.split(" ");
                for (String word : words_list) {
                    word = word.toLowerCase();
                    matcher = matchPattern.matcher(word);
                    if (matcher.matches()) {
                        String[] temp = word.split("([^a-záéíóúüñ]+)");
                        if (temp.length == 1) {
                            word = temp[0];
                        } else {
                            word = temp[1];
                        }
                        Word wordObject = new Word(word);
                        if (!words.isEmpty()) {
                            Word x = words.get(wordObject.hashCode());
                            if (x != null) {
                                x.addCount();
                                continue;
                            }
                        }
                        words.put(wordObject.hashCode(), wordObject);
                    }
                }
                textLine = bufferedReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Error de apertura");
        } catch (IOException ex) {
            System.out.println("Error al cerrar archivo");
        }

    }

    public String wordPattern(){
        return "([^a-záéíóúüñ0-9]*)([a-záéíóúüñ]+)([^a-záéíóúüñ0-9]*)";
    }

    @Override
    public String toString() {
        String str = "TextFile{" + "file=" + file + ": }";
        str += this.words.toString();
        return str;
    }
}
