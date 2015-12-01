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

import static hrcek.core.Boot.printIfNotVerbose;
import static hrcek.core.Boot.printIfVerbose;
import static hrcek.core.Boot.showUsage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyManager {

    public static List<String> sources = new ArrayList<>();
    public static List<Integer> weights = new ArrayList<>();
    public static int accuracy = 1;
    public static int accuracy_write = 0;
    public static int dictionary_accuracy = 0;
    public static int output = 100;
    public static String outputFile = null;
    public static String inputDictionary = null;
    public static String outputDictionary = null;
    public static int threads = 1;
    public static boolean ignorePunctuation = false;
    public static boolean write = true;
    public static boolean VERBOSE = false;
    public static boolean formatText = true;
    public static boolean threadable = true;
    public static int fileOutputFormat = DerpyFormatter.DERPY_FORMAT_PLAINTEXT;
    public static int fileInputFormat = DerpyFormatter.DERPY_FORMAT_TEXT;

    private static boolean hasBeenRead = false;

    private static Dictionary dictionary;

    //TODO:
    //  Add more methods
    //  Make interactions go through this.
    public static List<String> getSources() {
        return sources;
    }

    public static void setSources(List<String> sources) {
        DerpyManager.sources = sources;
    }

    public static boolean isHasBeenRead() {
        return hasBeenRead;
    }

    public static void setHasBeenRead(boolean hasBeenRead) {
        DerpyManager.hasBeenRead = hasBeenRead;
    }

    public static List<Integer> getWeights() {
        return weights;
    }

    public static void setWeights(List<Integer> weights) {
        DerpyManager.weights = weights;
    }

    public static int getAccuracy() {
        return accuracy;
    }

    public static void setAccuracy(int accuracy) {
        DerpyManager.accuracy = accuracy;
    }

    public static int getAccuracy_write() {
        return accuracy_write;
    }

    public static void setAccuracy_write(int accuracy_write) {
        DerpyManager.accuracy_write = accuracy_write;
    }

    public static int getDictionary_accuracy() {
        return dictionary_accuracy;
    }

    public static void setDictionary_accuracy(int dictionary_accuracy) {
        DerpyManager.dictionary_accuracy = dictionary_accuracy;
    }

    public static int getOutput() {
        return output;
    }

    public static void setOutput(int output) {
        DerpyManager.output = output;
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(String outputFile) {
        DerpyManager.outputFile = outputFile;
    }

    public static String getInputDictionary() {
        return inputDictionary;
    }

    public static void setInputDictionary(String inputDictionary) {
        DerpyManager.inputDictionary = inputDictionary;
    }

    public static String getOutputDictionary() {
        return outputDictionary;
    }

    public static void setOutputDictionary(String outputDictionary) {
        DerpyManager.outputDictionary = outputDictionary;
    }

    public static int getThreads() {
        return threads;
    }

    public static void setThreads(int threads) {
        DerpyManager.threads = threads;
    }

    public static boolean isIgnorePunctuation() {
        return ignorePunctuation;
    }

    public static void setIgnorePunctuation(boolean ignorePunctuation) {
        DerpyManager.ignorePunctuation = ignorePunctuation;
    }

    public static boolean shouldWrite() {
        return write;
    }

    public static void setWrite(boolean write) {
        DerpyManager.write = write;
    }

    public static boolean isVERBOSE() {
        return VERBOSE;
    }

    public static void setVERBOSE(boolean VERBOSE) {
        DerpyManager.VERBOSE = VERBOSE;
    }

    public static boolean isFormatText() {
        return formatText;
    }

    public static void setFormatText(boolean formatText) {
        DerpyManager.formatText = formatText;
    }

    public static boolean isThreadable() {
        return threadable;
    }

    public static void setThreadable(boolean threadable) {
        DerpyManager.threadable = threadable;
    }

    public static int getFileOutputFormat() {
        return fileOutputFormat;
    }

    public static void setFileOutputFormat(int fileOutputFormat) {
        DerpyManager.fileOutputFormat = fileOutputFormat;
    }

    public static int getFileInputFormat() {
        return fileInputFormat;
    }

    public static void setFileInputFormat(int fileInputFormat) {
        DerpyManager.fileInputFormat = fileInputFormat;
    }

    public static Dictionary getDictionary() {
        return dictionary;
    }

    public static void setDictionary(Dictionary dictionary) {
        DerpyManager.dictionary = dictionary;
    }

    public static void write() {
        printIfVerbose("Writing...");

        if (ignorePunctuation) {
            printIfVerbose("Ignoring logical punctuation...");
        }

        DerpyWriter.setIgnorePunctuation(ignorePunctuation); //This will allow end punctuation to be placed close together. If this is not wanted, this value should be false...
        DerpyWriter dw = new DerpyWriter(dictionary);
        List<String> paragraphs = dw.generateStory(output);

        if (formatText) {
            paragraphs = DerpyFormatter.formatParagraphs(paragraphs, fileOutputFormat);
        }

        printIfVerbose("Story created...");
        printIfVerbose("Determining write location...");

        if (outputFile == null) {
            printIfVerbose("Write location not found...");
            printIfVerbose("Dumping to console!\n");
            for (String paragraph : paragraphs) {
                System.out.println(paragraph);
                System.out.println("\n");
            }
            printIfVerbose("\n");
        } else {
            try {
                printIfVerbose("Dumping story to file...");
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
                for (String paragraph : paragraphs) {
                    writer.write(paragraph);
                    writer.write("\n");
                }
                writer.close();
                printIfVerbose("Finished dumping story...");
            } catch (IOException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Read in any text file the DerpyWriter will use.
     *
     * @throws InterruptedException
     */
    public static void readSources() throws InterruptedException {
        if (sources.size() != 0) {
            printIfVerbose("Sources detected...");
            if (threads > 1 && threadable) {

                printIfVerbose("Distributing work over " + threads + " threads...");

                int count = sources.size() / threads;
                if (sources.size() % threads != 0) {
                    count++;
                }

                for (int i = 0; i < count; i++) {
                    Thread t[] = new Thread[threads];
                    for (int o = 0; o < threads; o++) {
                        if ((i * threads) + o >= sources.size()) {
                            break;
                        }
                        DerpyReader derpyReader = new DerpyReader(dictionary, sources.get((i * threads) + o));
                        t[o] = new Thread(derpyReader);
                        t[o].run();
                    }
                    for (int o = 0; o < threads; o++) {
                        if ((i * threads) + o >= sources.size()) {
                            break;
                        }
                        t[o].join();
                    }
                }

                printIfVerbose("Sources read...");
            } else if (!threadable) {
                int largestWords = -1;
                int largestWeight = -1;
                for (int i = 0; i < sources.size(); i++) {
                    Dictionary tmp = new Dictionary();
                    new DerpyReader(tmp, sources.get(i)).run();
                    int tmpMax = tmp.getWordCount();
                    if (tmpMax > largestWords) {
                        largestWords = tmpMax;
                        largestWeight = weights.get(i);
                    }
                }
                for (int i = 0; i < sources.size(); i++) {
                    int myWords = ((largestWords * weights.get(i)) / largestWeight);
                    DerpyReader derpyReader = new DerpyReader(dictionary, sources.get(i), myWords);
                    derpyReader.run();
                }
                printIfVerbose("Sources read...");
            } else {
                for (int i = 0; i < sources.size(); i++) {
                    DerpyReader derpyReader = new DerpyReader(dictionary, sources.get(i));
                    derpyReader.run();
                }

                printIfVerbose("Sources read...");
            }
        }
    }

    /**
     * Saves a local dictionary into file.
     */
    public static void saveDictionary() {
        try {
            printIfVerbose("Dumping dictionary...");
            if (inputDictionary != null) {
                printIfVerbose("Returning accuracy to dictionary accuracy...");
                Word.setAccuracyNumber(dictionary_accuracy);
                dictionary.regenerateLastWords();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputDictionary))));
            oos.writeInt(Word.accuracyNumber);
            for (Word word : dictionary.getWordList()) {
                oos.writeObject(word);
                oos.flush();
            }
            oos.close();
            printIfVerbose("Dictionary dumped...");
        } catch (IOException e) {
            System.err.println("Unable to save file! Ignoring any changes made!");
        }
    }

    /**
     * Method to load a file of words into a local dictionary
     */
    public static void loadDictionary() {
        try {
            printIfVerbose("Loading dictionary...");

            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(inputDictionary))));
            dictionary_accuracy = accuracy = ois.readInt();

            printIfVerbose("Dictionary accuracy read... " + accuracy);
            printIfVerbose("Reading words...");

            boolean hasWords = true;
            while (hasWords) {
                try {
                    dictionary.addWord((Word) ois.readObject());
                } catch (Exception e) {
                    printIfVerbose("Finished reading words...");
                    printIfVerbose("Total word count: " + dictionary.getSize());
                    hasWords = false;
                }
            }
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();

            printIfVerbose("The dictionary at " + inputDictionary + " could not be loaded...");
            printIfVerbose("Using empty dictionary!");
            printIfNotVerbose("Dictionary not found! Using empty dictionary...");
        }
    }

    public static void setWordAccuracy() {
        printIfVerbose("Setting accuracy to " + accuracy + "...");

        Word.setAccuracyNumber(accuracy); //2-3 for songs, more for texts
        dictionary.regenerateLastWords();
    }

    public static void checkIfRequestedAccuracyIsWithinAcceptableBounds() {
        if (Word.accuracyNumber > accuracy_write && accuracy_write != 0) {
            printIfVerbose("Requested accuracy is within acceptable parameters...");
            printIfVerbose("Setting accuracy to " + accuracy_write);
            Word.setAccuracyNumber(accuracy_write);
            dictionary.regenerateLastWords();
        }
    }

    public static boolean checkIfHasWritingSource() {
        if (DerpyManager.getSources().size() < 1 && DerpyManager.getInputDictionary() == null) {
            return false;
        }
        return true;
    }

}
