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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyReader implements Runnable {

    final Dictionary dictionary;
    final String fileLocation;
    final int numWords;

    public DerpyReader(Dictionary dictionary, String fileLocation) {
        this.dictionary = dictionary;
        this.fileLocation = fileLocation;
        this.numWords = -1;
    }

    public DerpyReader(Dictionary dictionary, String fileLocation, int numWords) {
        this.dictionary = dictionary;
        this.fileLocation = fileLocation;
        this.numWords = numWords;
    }

    /**
     * This function reads through the file specified in fileLocation.
     *
     * @TODO Add additional error checking
     *
     * @throws hrcek.core.DerpyReader.NoFileLocationException
     * @throws hrcek.core.DerpyReader.FileErrorException
     */
    private void readThroughFile() throws NoFileLocationException, IOException {

        if (fileLocation == null || fileLocation == "") {
            throw new NoFileLocationException("File location is null!");
        }

        //TODO ADD ERROR CHECKING
        if (numWords < 0) {
            BufferedReader fileReader = new BufferedReader(new FileReader(fileLocation));

            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.length() > 0) {
                    line = DerpyFormatter.unformatText(line, DerpyManager.getFileInputFormat());
                    String[] words = line.split(" ");
                    DerpyReader.mergeWords(words, DerpyManager.getFileInputFormat());
                    for (String word : words) {
                        DerpyReader.addWord(word, DerpyManager.getFileInputFormat());
                    }
                }
            }
        } else {
            int initSize = dictionary.getWordCount();
            while (dictionary.getWordCount() - initSize < numWords) {
                BufferedReader fileReader = new BufferedReader(new FileReader(fileLocation));
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    if (line.length() > 0) {
                        line = DerpyFormatter.unformatText(line);
                        String[] words = line.split(" ");
                        DerpyReader.mergeWords(words, DerpyManager.getFileInputFormat());
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                dictionary.addWord(word.toLowerCase());
                            }
                            if (dictionary.getWordCount() - initSize >= numWords) {
                                fileReader.close();
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public void run() {
        try {
            readThroughFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This function is used to determine if a word is actually a punctuation.
     *
     * @param word Word to be checked
     * @return If the word is a punctuation
     */
    public static boolean isPunctuation(Word word) {
        if (word instanceof Punctuation) {
            return true;
        }
        return false;
    }

    public static void mergeWords(String[] wordList, int fileInputFormat) {
        for (int index = wordList.length - 1; index >= 0; index--) {
            switch (fileInputFormat) {
                case DerpyFormatter.DERPY_FORMAT_HTML:
                    if (wordList[index].equals(">")) {
                        int origIndex = index;
                        while (index > 0) {
                            if (wordList[index--].equals("<")) {
                                String newWord = wordList[index] + " ";
                                for (int i = index + 1; i <= origIndex; i++) {
                                    newWord += wordList[i] + " ";
                                    wordList[i] = "";
                                }
                                wordList[index] = newWord;
                                break;
                            }
                        }
                    }
            }
        }
    }

    public static void addWord(String word, int fileInputType) {
        switch (fileInputType) {
            case DerpyFormatter.DERPY_FORMAT_HTML:
                if (!word.equals("") && !word.equals(" ")) {
                    if (word.startsWith("<") && word.endsWith(">")) {
                        DerpyManager.getDictionary().addTag(word.toLowerCase());
                    } else {
                        DerpyManager.getDictionary().addWord(word.toLowerCase());
                    }
                }
                break;
            default:
                if (!word.equals("") && !word.equals(" ")) {
                    DerpyManager.getDictionary().addWord(word.toLowerCase());
                }
        }
    }

    public static boolean isEndPunctuation(Word word) {
        if (!isPunctuation(word)) {
            return false;
        }

        for (String punct : Punctuation.endPunctuations) {
            if (punct.equals(word.getName())) {
                return true;
            }
        }

        return false;
    }

    private class FileErrorException extends Exception {

        public FileErrorException(String message) {
            super(message);
        }
    }

    private class NoFileLocationException extends Exception {

        public NoFileLocationException(String message) {
            super(message);
        }
    }

}
