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
    public static int accuracy_write = 0;
    public static int dictionary_accuracy = 0;
    public static int output = 100;
    public static String outputFile = null;
    public static String inputDictionary = null;
    public static String outputDictionary = null;
    public static int threads = 1;
    public static boolean ignorePunctuation = false;
    public static boolean write = true;
    public static boolean verbose = false;

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
        System.out.println("\t-v                verbose mode");
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
                    accuracy = Integer.parseInt(args[++i]);
                    accuracy_write = accuracy;
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
            } else if (args[i].equals("-v")) {
                verbose = true;
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
            } else if (args[i].equals("-w")) {
                try {
                    output = Integer.parseInt(args[++i]);
                    if (output <= 0) {
                        System.out.println("Argument must be a positive integer");
                        System.exit(0);
                    } else {
                        i++;
                        for (; output > 0; output--) {
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
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer");
                    System.exit(0);
                }
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
                if (verbose) {
                    System.out.println("Loading dictionary...");
                }

                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(inputDictionary))));
                dictionary_accuracy = accuracy = ois.readInt();

                if (verbose) {
                    System.out.println("Dictionary accuracy read... " + accuracy);
                    System.out.println("Reading words...");
                }

                boolean hasWords = true;
                while (hasWords) {
                    try {
                        dictionary.addWord((Word) ois.readObject());
                    } catch (Exception e) {
                        if (verbose) {
                            System.out.println("Finished reading words...");
                            System.out.println("Total word count: " + dictionary.getSize());
                        }
                        hasWords = false;
                    }
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (verbose) {
                    System.out.println("The dictionary at " + inputDictionary + " could not be loaded...");
                    System.out.println("Using empty dictionary!");
                } else {
                    System.err.println("Dictionary not found! Using empty dictionary...");
                }
            }
        }

        if (verbose) {
            System.out.println("Setting accuracy to " + accuracy + "...");
        }
        Word.setAccuracyNumber(accuracy); //2-3 for songs, more for texts
        dictionary.regenerateLastWords();

        if (sources.size() != 0) {
            if (verbose) {
                System.out.println("Sources detected...");
            }
            if (threads > 1) {
                if (verbose) {
                    System.out.println("Distributing work over " + threads + "threads...");
                }
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
                    if (verbose) {
                        System.out.println("Sources read...");
                    }
                }
            } else {
                for (int i = 0; i < sources.size(); i++) {
                    DerpyReader derpyReader = new DerpyReader(dictionary);
                    derpyReader.setFileLocation(sources.get(i));
                    derpyReader.run();
                }
                if (verbose) {
                    System.out.println("Sources read...");
                }
            }
        }

        if (Word.accuracyNumber > accuracy_write && accuracy_write != 0) {
            if (verbose) {
                System.out.println("Requested accuracy is within acceptable parameters...");
                System.out.println("Setting accuracy to " + accuracy_write);
            }
            Word.setAccuracyNumber(accuracy_write);
            dictionary.regenerateLastWords();
        }

        if (write) {
            if (verbose) {
                System.out.println("Writing...");
                if (ignorePunctuation) {
                    System.out.println("Ignoring logical punctuation...");
                }
            }
            DerpyWriter.setIgnorePunctuation(ignorePunctuation); //This will allow end punctuation to be placed close together. If this is not wanted, this value should be false...
            DerpyWriter dw = new DerpyWriter(dictionary);
            String outString = dw.generateStory(output).replaceAll(" \\.", ".").replaceAll(" \\,", ",").replaceAll(" !", "!") + "\n";

            if (verbose) {
                System.out.println("Story created...");
                System.out.println("Determining write location...");
            }

            if (outputFile == null) {
                if (verbose) {
                    System.out.println("Write location not found...);");
                    System.out.println("Dumping to console!\n");
                }
                System.out.println(dw);
                if (verbose) {
                    System.out.println("\n");
                }
            } else {
                try {
                    if (verbose) {
                        System.out.println("Dumping story to file...");
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
                    writer.write(outString);
                    writer.close();
                    if (verbose) {
                        System.out.println("Finished dumping story...");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (!write && verbose) {
            System.out.println("Write skipped...");
        }

        if (outputDictionary != null) {
            try {
                if (verbose) {
                    System.out.println("Dumping dictionary...");
                }
                if (inputDictionary != null) {
                    if (verbose) {
                        System.out.println("Returning accuracy to dictionary accuracy...");
                    }
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
                if (verbose) {
                    System.out.println("Dictionary dumped...");
                }
            } catch (IOException e) {
                System.err.println("Unable to save file! Ignoring any changes made!");
            }
        }

    }

}
