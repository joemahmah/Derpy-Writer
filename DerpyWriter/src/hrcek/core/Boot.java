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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 * @author Xperia64
 */
public class Boot {

    public static void showUsage() {
        System.out.println("Usage:");
        System.out.println("java -jar DerpyWriter.jar <arguments>\n");
        System.out.println("\tArguments:");
        System.out.println("\t<source files>        plaintext files used for source");
        System.out.println("\t-a [#]                accuracy (default 1)");
        System.out.println("\t-c [#]                output count (default 100)");
        System.out.println("\t-h      --help        display this text");
        System.out.println("\t-o [FILE]             output file (default stdout, hyphen for stdout)");
        System.out.println("\t-t [#]                thread count (default 1)");
        System.out.println("\t-i                    ignore logical punctuation checking.");
        System.out.println("\t-l [FILE]             load dictionary file.");
        System.out.println("\t-s [FILE]             save dictionary file.");
        System.out.println("\t-r                    only read files.");
        System.out.println("\t-v                    verbose mode");
        System.out.println("\t-w [#] [FILE]         weight a file relative to the other files");
        System.out.println("\t-nf                   do not format text");
        System.out.println("\t-fo <txt,html>        Output text as a format (Default plaintext)");
        System.out.println("\t-fi <txt,normal,html> Input text as a format (Default normal)");
    }

    public static String showUsageAsString() {
        String msg = "";

        msg += "Usage:";
        msg += "java -jar DerpyWriter.jar <arguments>\n\n";
        msg += "\tArguments:\n";
        msg += "\t<source files>        plaintext files used for source\n";
        msg += "\t-a [#]                accuracy (default 1)\n";
        msg += "\t-c [#]                output count (default 100)\n";
        msg += "\t-h      --help        display this text\n";
        msg += "\t-o [FILE]             output file (default stdout, hyphen for stdout)\n";
        msg += "\t-t [#]                thread count (default 1)\n";
        msg += "\t-i                    ignore logical punctuation checking.\n";
        msg += "\t-l [FILE]             load dictionary file.\n";
        msg += "\t-s [FILE]             save dictionary file.\n";
        msg += "\t-r                    only read files.\n";
        msg += "\t-v                    verbose mode\n";
        msg += "\t-w [#] [FILE]         weight a file relative to the other files\n";
        msg += "\t-nf                   do not format text\n";
        msg += "\t-fo <txt,html>        Output text as a format (Default plaintext)\n";
        msg += "\t-fi <txt,normal,html> Input text as a format (Default normal)\n";

        return msg;
    }

    /**
     *
     * @param file the filename.
     * @return true if the file can be opened.
     */
    public static boolean isFilenameValid(String file) {
        final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
        for(int i = 0; i<ILLEGAL_CHARACTERS.length; i++)
        {
            if(file.contains(Character.toString(ILLEGAL_CHARACTERS[i])))
            {
                return false;
            }
        }
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        if (args.length == 0) {
            DerpyGUI gui = new DerpyGUI();
        } else {

            checkFlags(args);
            if (!DerpyManager.checkIfHasWritingSource()) {
                System.out.println("This requires at least one source file");
                showUsage();
                System.exit(1);
            }

            DerpyManager.setDictionary(new Dictionary());

            if (DerpyManager.getInputDictionary() != null) {
                DerpyManager.loadDictionary();
            }

            DerpyManager.setWordAccuracy();
            DerpyManager.readSources();
            DerpyManager.checkIfRequestedAccuracyIsWithinAcceptableBounds();

            if (DerpyManager.shouldWrite()) {
                System.out.println(DerpyManager.write());
            } else {
                printIfVerbose("Write skipped...");
            }

            if (DerpyManager.getOutputDictionary() != null) {
                DerpyManager.saveDictionary();
            }
        }
    }

    public static void printIfVerbose(String msg) {
        if (DerpyManager.isVERBOSE()) {
            System.out.println(msg);
        }
    }

    public static void printIfNotVerbose(String msg) {
        if (!DerpyManager.isVERBOSE()) {
            System.out.println(msg);
        }
    }

    /**
     * Method for checking command line flags
     *
     * @param args The arguments
     */
    public static void checkFlags(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-a")) {
                try {
                    DerpyManager.setAccuracy(Integer.parseInt(args[++i]));
                    DerpyManager.setAccuracy_write(DerpyManager.getAccuracy());
                    if (DerpyManager.getAccuracy() < 0) {
                        System.out.println("Argument must be a positive integer");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer");
                    System.exit(0);
                }
            } else if (args[i].equals("-c")) {
                try {
                    DerpyManager.setOutput(Integer.parseInt(args[++i]));
                    if (DerpyManager.getOutput() < 0) {
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
                    DerpyManager.setOutputFile(args[i]);
                }
            } else if (args[i].equals("-l")) {
                if (!args[++i].equals("-")) {
                    DerpyManager.setInputDictionary(args[i]);
                }
            } else if (args[i].equals("-v")) {
                DerpyManager.setVERBOSE(true);
            } else if (args[i].equals("-nf")) {
                DerpyManager.setFormatText(false);
            } else if (args[i].equals("-s")) {
                if (!args[++i].equals("-")) {
                    DerpyManager.setOutputDictionary(args[i]);
                }
            } else if (args[i].equals("-t")) {
                try {
                    DerpyManager.setThreads(Integer.parseInt(args[++i]));
                    if (DerpyManager.getThreads() < 1) {
                        System.out.println("Argument must be a positive integer greater than 1");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer greater than 1");
                    System.exit(0);
                }
            } else if (args[i].equals("-i")) {
                DerpyManager.setIgnorePunctuation(true);
            } else if (args[i].equals("-r")) {
                DerpyManager.setWrite(false);
            } else if (args[i].equals("-w")) {
                try {
                    int weight = Integer.parseInt(args[++i]);
                    if (weight <= 0) {
                        System.out.println("Argument must be a positive integer");
                        System.exit(0);
                    } else {
                        ++i;
                        DerpyManager.setThreadable(false);
                        if (isFilenameValid(args[i])) {
                            if (new File(args[i]).exists()) {
                                DerpyManager.getSources().add(new File(args[i]).getAbsolutePath());
                                DerpyManager.getWeights().add(weight);
                            }
                        } else if (args[i].toLowerCase().equals("*stdin*")) {
                            if (DerpyManager.stdin == null) {
                                BufferedReader derpReader = new BufferedReader(new InputStreamReader(System.in));
                                StringBuilder builder = new StringBuilder();
                                String aux = "";

                                try {
                                    while ((aux = derpReader.readLine()) != null) {
                                        builder.append(aux);
                                        builder.append('\n');
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                DerpyManager.stdin = builder.toString();
                            }
                            DerpyManager.getSources().add("*STDIN*");
                            DerpyManager.getWeights().add(weight);
                        } else {
                            System.out.println("Invalid filename: " + args[i]);
                            System.exit(0);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Argument must be a positive integer");
                    System.exit(0);
                }
            } else if (args[i].equals("-fo")) {
                i++;

                if (args[i].toLowerCase().equals("plaintext") || args[i].toLowerCase().equals("text") || args[i].toLowerCase().equals("txt")) {
                    DerpyManager.setFileOutputFormat(DerpyFormatter.DERPY_FORMAT_PLAINTEXT);
                } else if (args[i].toLowerCase().equals("html") || args[i].toLowerCase().equals("htm")) {
                    DerpyManager.setFileOutputFormat(DerpyFormatter.DERPY_FORMAT_HTML);
                }

            } else if (args[i].equals("-fi")) {
                i++;

                if (args[i].toLowerCase().equals("plaintext") || args[i].toLowerCase().equals("text") || args[i].toLowerCase().equals("txt")) {
                    DerpyManager.setFileInputFormat(DerpyFormatter.DERPY_FORMAT_PLAINTEXT);
                } else if (args[i].toLowerCase().equals("html") || args[i].toLowerCase().equals("htm")) {
                    DerpyManager.setFileInputFormat(DerpyFormatter.DERPY_FORMAT_HTML);
                } else if (args[i].toLowerCase().equals("normal") || args[i].toLowerCase().equals("norm")) {
                    DerpyManager.setFileInputFormat(DerpyFormatter.DERPY_FORMAT_TEXT);
                }

            } else {
                // Assume a relative path if not absolute
                if (isFilenameValid(args[i])) {
                    if (new File(args[i]).exists()) {
                        DerpyManager.getSources().add(new File(args[i]).getAbsolutePath());
                        DerpyManager.getWeights().add(1);
                    }
                } else if (args[i].toLowerCase().equals("*stdin*")) {
                    if (DerpyManager.stdin == null) {
                        BufferedReader derpReader = new BufferedReader(new InputStreamReader(System.in));
                        StringBuilder builder = new StringBuilder();
                        String aux = "";

                        try {
                            while ((aux = derpReader.readLine()) != null) {
                                builder.append(aux);
                                builder.append('\n');
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        DerpyManager.stdin = builder.toString();
                    }
                    DerpyManager.getSources().add("*STDIN*");
                    DerpyManager.getWeights().add(1);
                } else {
                    System.out.println("Invalid filename: " + args[i]);
                    System.exit(0);
                }
            }
        }
    }

}
