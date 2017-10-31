package textManager;

import java.io.Serializable;

public class Word implements Comparable<Word>, Serializable {

    private final String word;
    private int count;

    /**
     * Creates a new word object saving wht word and a count attribute
     * @param word is the word to save
     */
    public Word(String word) {
        this.word = word;
        count = 1;
    }

    /**
     * @return word The word save in the Word instance
     */
    public String getWord() {
        return this.word;
    }

    /**
     * @return count Return the counter of words
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Adds 1 to the Word class counter
     */
    public void addCount() {
        this.count ++;
    }

    @Override
    public String toString() {
        return this.word + ": " + this.count;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.word.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Word)) {
            return false;
        }

        Word x = (Word) obj;
        return this.word.equals(x.word);
    }

    @Override
    public int compareTo(Word o) {
        return this.word.compareTo(o.word);
    }
}
