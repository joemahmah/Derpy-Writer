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
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class LogicFactory {

    private static volatile Random random = new Random();

    public static synchronized int getRandomInt() {
        return random.nextInt();
    }

    public static synchronized int getRandomInt(int max) {
        return random.nextInt(max);
    }

    public static synchronized int getRandomInt(int min, int max) {
        return min + getRandomInt(max);
    }

    public static synchronized boolean getBoolean(int num, int denom) {
        int i = getRandomInt(denom);
        if (i < num) {
            return true;
        }

        return false;
    }

    public synchronized static Word getRandomWord(Word[] currentWord, Dictionary dictionary) {
        List<Word> wordList = new ArrayList();

        for (Word word : currentWord[0].getWordsAfter(0).keySet()) {
            int count = currentWord[0].getWordsAfter(0).get(word);
            for (int i = 0; i < count; i++) {
                wordList.add(word);
            }

        }

        for (int i = 1; i < Word.accuracyNumber; i++) {
            for (Word word : currentWord[i].getWordsAfter(i).keySet()) {
                int count = currentWord[i].getWordsAfter(i).get(word);
                for (int j = 0; j < count && wordList.contains(word); j++) {
                    if ((!word.equals(dictionary.getWord(".")) && !word.equals(dictionary.getWord("!")) && !word.equals(dictionary.getWord("?"))) || DerpyWriter.ignoresPunctuation()) {
                        wordList.add(word);
                    }
                }
            }
        }

        for (int i = Word.accuracyNumber - 1; i > 0; i--) {
            currentWord[i] = currentWord[i - 1];
        }

        if (wordList.size() > 0) {
            currentWord[0] = wordList.get(getRandomInt(wordList.size()));
            return currentWord[0];
        } else {
            currentWord[0] = Word.wordNotFound;
            return Word.wordNotFound;
        }

    }

    public synchronized static Word getRandomWord(Dictionary dictionary) {
        return dictionary.getWord(getRandomInt(dictionary.getSize()));
    }

    public synchronized static boolean isSentenceEnd(Word word) {

        if (word.getName().equals(".") || word.getName().equals("!") || word.getName().equals("?") || word.getName().equals("...")) {
            return true;
        }

        return false;

    }

}
