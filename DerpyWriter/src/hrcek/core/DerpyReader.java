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

    Dictionary dictionary;
    String fileLocation;

    public DerpyReader(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.fileLocation = "";
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
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
        BufferedReader fileReader = new BufferedReader(new FileReader(fileLocation));

        while (fileReader.ready()) {
            String line = fileReader.readLine();
            if (line.length() > 0) {
                line = splitPunctuation(line);
                String[] words = line.split(" ");
                for (String word : words) {
                    if (word != "" && word != " ") {
                        dictionary.addWord(word);
                    }
                }
            }
        }

    }

    private String splitPunctuation(String str) {
        str = str.replaceAll("\\.", " .");
        str = str.replaceAll(",", " ,");
        str = str.replaceAll("\\?", " ?");
        str = str.replaceAll("!", " !");
        str = str.replaceAll("\"", " \" ");
        str = str.replaceAll("\\(", " ( ");
        str = str.replaceAll("\\)", " ) ");
        str = str.replaceAll(";", " ;");
        str = str.replaceAll("\t", " ");
        str = str.replaceAll("  ", " ");
        return str;
    }

    @Override
    public void run() {
        try {
            readThroughFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
