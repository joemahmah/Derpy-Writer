/*
 * The MIT License
 *
 * Copyright 2015 Michael Hrcek <hrcekmj@clarkson.edu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hrcek.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds words and acts as a buffer between the reader and writer. It
 * stores a list of words that can be used for writing as well as a list of
 * words last used in writing.
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class Dictionary implements Serializable {

    private volatile List<Word> words;
    private volatile List<Word> lastWords;
    static final long serialVersionUID = -3010695769693014199L;

    int lastPunctuation = 0;

    /**
     * This is the default constructor for the dictionary.
     */
    public Dictionary() {
        words = new ArrayList<>();
        lastWords = new ArrayList<Word>();

        for (int i = 0; i < Word.accuracyNumber; i++) {
            lastWords.add(Word.wordNotFound);
        }

    }

    /**
     * This method is needed if the word accuracy has changed. This allows the
     * reader and writer to function properly.
     */
    public void regenerateLastWords() {
        lastWords = new ArrayList<Word>();

        for (int i = 0; i < Word.accuracyNumber; i++) {
            lastWords.add(Word.wordNotFound);
        }
    }

    /**
     * This method adds a word to the dictionary.
     *
     * @param word Word to be added.
     */
    public synchronized void addWord(Word word) {
        words.add(word);
    }

    /**
     * Gets the number of words in the dictionary.
     *
     * @return Words in the dictionary.
     */
    public synchronized int getSize() {
        return words.size();
    }

    public synchronized int getWordCount() {
        int totalSize = 0;
        for (Word word : words) {
            totalSize += word.getRarity();
        }
        return totalSize;
    }

    /**
     * Gets the word at index.
     *
     * @param index The index of the word.
     * @return The word at the index.
     */
    public synchronized Word getWord(int index) {
        return words.get(index);
    }

    /**
     * Adds a word based on a string representation.
     *
     * @param name
     */
    public synchronized void addWord(String name) {
        for (int i = Word.accuracyNumber - 1; i >= 0; i--) {
            lastWords.get(i).addWordAfter(getWord(name), i);
        }

        for (int i = Word.accuracyNumber - 1; i > 0; i--) {
            lastWords.set(i, lastWords.get(i - 1));
        }
        lastWords.set(0, getWord(name));
        lastWords.get(0).increaseRarity();

        if (DerpyReader.isPunctuation(getWord(name))) {
            ((Punctuation) getWord(name)).addLength(lastPunctuation);
            lastPunctuation = 0;
        } else {
            lastPunctuation++;
        }
    }

    /**
     * Adds a word based on a string representation.
     *
     * @param name
     */
    public synchronized void addTag(String name) {
        for (int i = Word.accuracyNumber - 1; i >= 0; i--) {
            lastWords.get(i).addWordAfter(getWord(name), i);
        }

        for (int i = Word.accuracyNumber - 1; i > 0; i--) {
            lastWords.set(i, lastWords.get(i - 1));
        }
        lastWords.set(0, getWord(name));
        lastWords.get(0).increaseRarity();

        if (DerpyReader.isPunctuation(getWord(name))) {
            ((Punctuation) getWord(name)).addLength(lastPunctuation);
            lastPunctuation = 0;
        } else {
            lastPunctuation++;
        }
    }

    /**
     * Gets the word based on a string representation of the word. It the word
     * does not exist, it is created and added to the dictionary using the
     * string representation given.
     *
     * @param name The string representation of the word.
     * @return Word corresponding to the string representation.
     */
    public synchronized Word getWord(String name) {

        for (Word word : words) {
            if (word.getName().equals(name)) {
                return word;
            }
        }

        for (String s : Punctuation.punctuations) {
            if (s.equals(name)) {
                Punctuation newWord = new Punctuation(name);
                words.add(newWord);
                return newWord;
            }
        }

        Word newWord = new Word(name);
        words.add(newWord);
        return newWord;
    }

    /**
     * Gets the word based on a string representation of the word. It the word
     * does not exist, it is created and added to the dictionary using the
     * string representation given.
     *
     * @param name The string representation of the word.
     * @return Word corresponding to the string representation.
     */
    public synchronized Tag getTag(String name) {

        String[] parts = name.split(" ");
        String partName = parts[1];
        String[] partsParams = new String[parts.length - 3];
        for (int i = 2; i < parts.length - 1; i++) {
            partsParams[i - 2] = parts[i];
        }

        for (Word word : words) {
            if (word instanceof Tag) {
                if (word.getName().equals(partName)) {
                    return (Tag) word;
                }
            }
        }

        Tag newWord = new Tag(partName, partsParams);
        words.add(newWord);
        return newWord;
    }

    /**
     * Determines if a word exists in the dictionary.
     *
     * @param name String representation of the word.
     * @return If the word exists.
     */
    public synchronized boolean hasWord(String name) {
        for (Word word : words) {
            if (word.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method prints the contents of the dictionary. Prints to standard
     * output.
     */
    public synchronized void printContents() {
        for (Word word : words) {
            System.out.println(word);
        }
    }

    /**
     * Gets the list of words in the dictionary.
     *
     * @return The list of words in the dictionary.
     */
    public List<Word> getWordList() {
        return words;
    }

    /**
     * A typical toString() method... Creates a string containing all of the
     * string representations of the words in the dictionary. They are separated
     * by new lines.
     *
     * @return The dictionary as a string.
     */
    public String toString() {
        String s = "";
        for (Word word : words) {
            s += word + "\n";
        }
        return s;
    }

}
