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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class holds a number of functions used by the derpy reader and writer.
 * It has generic functions for things like random integers. It also holds logic
 * to get random words based on a dictionary object or a word object.
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class LogicFactory {

    private static volatile Random random = new Random();

    /**
     * This function gets a random positive integer.
     *
     * @return A random integer.
     */
    public static synchronized int getRandomInt() {
        return random.nextInt();
    }

    /**
     * This function gets a random integer between 0 and a user defined end.
     *
     * @param max Maximum number the function may return.
     * @return A number between 0 and a user defined max.
     */
    public static synchronized int getRandomInt(int max) {
        return random.nextInt(max);
    }

    /**
     * This function gets a random integer between a user defined minimum and a
     * user defined maximum.
     *
     * @param min User defined minimum value.
     * @param max User defined maximum value.
     * @return A random number between min and max.
     */
    public static synchronized int getRandomInt(int min, int max) {
        return min + getRandomInt(max);
    }

    /**
     * This function returns a random weighted boolean. This function uses a
     * fractional weighting with the average chance being about equal to
     * num/denom.
     *
     * @param num The numerator for the fractional weighting.
     * @param denom The denominator for the fractional weighting.
     * @return A fractionally weighted boolean.
     */
    public static synchronized boolean getBoolean(int num, int denom) {
        int i = getRandomInt(denom);
        if (i < num) {
            return true;
        }

        return false;
    }

    /**
     * This function picks a random word based upon previously used words. It
     * weighs words based on their likelihood to appear after x words.
     *
     * @param pastWords Array of past words. The size of the array is based on
     * accuracy.
     * @param dictionary The dictionary pulling from.
     * @return
     */
    public synchronized static Word getRandomWord(Word[] pastWords, Dictionary dictionary) {
        List<Word> wordList = new ArrayList();

        for (String word : pastWords[0].getWordsAfter(0).keySet()) {
            int count = pastWords[0].getWordsAfter(0).get(word);
            for (int i = 0; i < count; i++) {
                wordList.add(dictionary.getWord(word));
            }

        }

        for (int i = 1; i < Word.accuracyNumber; i++) {
            for (String word : pastWords[i].getWordsAfter(i).keySet()) {
                int count = pastWords[i].getWordsAfter(i).get(word);
                for (int j = 0; j < count && wordList.contains(word); j++) {
                    if ((!word.equals(dictionary.getWord(".")) && !word.equals(dictionary.getWord("!")) && !word.equals(dictionary.getWord("?"))) || DerpyWriter.ignoresPunctuation()) {
                        wordList.add(dictionary.getWord(word));
                    }
                }
            }
        }

        for (int i = Word.accuracyNumber - 1; i > 0; i--) {
            pastWords[i] = pastWords[i - 1];
        }

        if (wordList.size() > 0) {
            pastWords[0] = wordList.get(getRandomInt(wordList.size()));
            return pastWords[0];
        } else {
            pastWords[0] = Word.wordNotFound;
            return Word.wordNotFound;
        }

    }

    /**
     * This function randomly gets a word form the dictionary.
     *
     * @param dictionary Dictionary to be used.
     * @return A random word.
     */
    public synchronized static Word getRandomWord(Dictionary dictionary) {
        return dictionary.getWord(getRandomInt(dictionary.getSize()));
    }

}
