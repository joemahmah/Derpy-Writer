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
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class Dictionary implements Serializable {

    private volatile List<Word> words;
    private volatile List<Word> lastWords;

    public Dictionary() {
        words = new ArrayList<>();
        lastWords = new ArrayList<Word>();

        for (int i = 0; i < Word.accuracyNumber; i++) {
            lastWords.add(Word.wordNotFound);
        }

    }

    public synchronized int getSize() {
        return words.size();
    }

    public synchronized Word getWord(int index) {
        return words.get(index);
    }

    public synchronized void addWord(String name) {
        for (int i = Word.accuracyNumber - 1; i >= 0; i--) {
            lastWords.get(i).addWordAfter(getWord(name),i);
        }
        
        for(int i = Word.accuracyNumber -1; i > 0; i--){
            lastWords.set(i,lastWords.get(i-1));
        }
        lastWords.set(0, getWord(name));
    }

    public synchronized Word getWord(String name) {
        for (Word word : words) {
            if (word.getName().equals(name)) {
                return word;
            }
        }

        Word newWord = new Word(name);
        words.add(newWord);
        return newWord;
    }

    public boolean hasWord(String name) {
        for (Word word : words) {
            if (word.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void printContents() {
        for (Word word : words) {
            System.out.println(word);
        }
    }

}
