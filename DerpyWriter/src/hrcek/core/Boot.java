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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 * @author Xperia64
 */
public class Boot {

    /**
     * @param args the command line arguments
     */
    public static ArrayList<String> sources = new ArrayList<String>();
    public static int accuracy = 1;
    public static int output = 100;
    public static String outputFile = null;
    public static String inputDictionary = null;
    public static String outputDictionary = null;
    public static int threads = 1;
    public static boolean ignorePunctuation = false;
    public static boolean write = true;

    public static void showUsage() {
        System.out.println("Usage:");
        System.out.println("java DerpyWriter <arguments>\n");
        System.out.println("\tArguments:");
        System.out.println("\t<source files>    plaintext files used for source");
        System.out.println("\t-a [#]            accuracy (default 1)");
        System.out.println("\t-c [#]            output count (default 100)");
        System.out.println("\t-h      --help    display this text");
        System.out.println("\t-o [FILE]         output file (default stdout, hyphen for stdout)");
        System.out.println("\t-t [#]            thread count (default 1)");
        System.out.println("\t-i                ignore logical punctuation checking.");
        System.out.println("\t-l [FILE]         load dictionary file.");
        System.out.println("\t-s [FILE]         save dictionary file.");
        System.out.println("\t-r                only read files.");
    }

    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Must have at least one source file
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-a")) {
                try {
                    if (accuracy == 1) {
                        accuracy = Integer.parseInt(args[++i]);
                    }
                    if (accuracy < 0) {
                        System.out.println("Argument must be a positive integer");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer");
                    System.exit(0);
                }
            } else if (args[i].equals("-c")) {
                try {
                    output = Integer.parseInt(args[++i]);
                    if (output < 0) {
                        System.out.println("Argument must be a positive integer");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer");
                    System.exit(0);
                }
            } else if (args[i].equals("-h") || args[i].equals("--help")) {
                showUsage();
                System.exit(0);
            } else if (args[i].equals("-o")) {
                if (!args[++i].equals("-")) {
                    outputFile = args[i];
                }
            } else if (args[i].equals("-l")) {
                if (!args[++i].equals("-")) {
                    inputDictionary = args[i];
                }
            } else if (args[i].equals("-s")) {
                if (!args[++i].equals("-")) {
                    outputDictionary = args[i];
                }
            } else if (args[i].equals("-t")) {
                try {
                    threads = Integer.parseInt(args[++i]);
                    if (threads < 1) {
                        System.out.println("Argument must be a positive integer greater than 1");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer greater than 1");
                    System.exit(0);
                }
            } else if (args[i].equals("-i")) {
                ignorePunctuation = true;
            } else if (args[i].equals("-r")) {
                write = false;
            } else {
                // Assume a relative path if not absolute
                if (isFilenameValid(args[i])) {
                    if (new File(args[i]).exists()) {
                        sources.add(new File(args[i]).getAbsolutePath());
                    }
                } else {
                    System.out.println("Invalid filename: " + args[i]);
                    System.exit(0);
                }
            }
        }
        if (sources.size() < 1 && inputDictionary == null) {
            System.out.println("This requires at least one source file");
            showUsage();
            System.exit(0);
        }

        Dictionary dictionary = new Dictionary();

        if (inputDictionary != null) {
            try {
                System.out.println("Loading dictionary...");
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(inputDictionary))));
                accuracy = ois.readInt();

                boolean hasWords = true;
                while (hasWords) {
                    try {
                        dictionary.addWord((Word) ois.readObject());
                    } catch (Exception e) {
                        hasWords = false;
                    }
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Dictionary not found! Using empty dictionary...");
            }
        }

        Word.setAccuracyNumber(accuracy); //2-3 for songs, more for texts
        dictionary.regenerateLastWords();
        
        if (threads > 1) {
            int count = sources.size() % threads;
            for (int i = 0; i < count + 1; i++) {
                Thread t[] = new Thread[threads];
                for (int o = 0; o < threads; o++) {
                    if ((i * threads) + o > sources.size()) {
                        break;
                    }
                    DerpyReader derpyReader = new DerpyReader(dictionary);
                    derpyReader.setFileLocation(sources.get((i * threads) + o));
                    t[o] = new Thread(derpyReader);
                    t[o].run();
                }
                for (int o = 0; o < threads; o++) {
                    if ((i * threads) + o > sources.size()) {
                        break;
                    }
                    t[o].join();
                }
            }
        } else {
            for (int i = 0; i < sources.size(); i++) {
                DerpyReader derpyReader = new DerpyReader(dictionary);
                derpyReader.setFileLocation(sources.get(i));
                derpyReader.run();
            }
        }

        if (write) {
            DerpyWriter.setIgnorePunctuation(ignorePunctuation); //This will allow end punctuation to be placed close together. If this is not wanted, this value should be false...
            DerpyWriter dw = new DerpyWriter(dictionary);
            String outString = dw.generateStory(output).replaceAll(" \\.", ".").replaceAll(" \\,", ",").replaceAll(" !", "!");
            if (outputFile == null) {
                System.out.println(dw);
            } else {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
                    writer.write(outString);
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (outputDictionary != null) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputDictionary))));
                oos.writeInt(Word.accuracyNumber);
                for (Word word : dictionary.getWordList()) {
                    oos.writeObject(word);
                    oos.flush();
                }
                oos.close();
            } catch (IOException e) {
                System.err.println("Unable to save file! Ignoring any changes made!");
            }
        }

    }

}
