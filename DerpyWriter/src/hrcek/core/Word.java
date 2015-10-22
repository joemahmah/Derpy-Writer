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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class Word implements Serializable {

    public static Word wordNotFound = new Word("/dev/erg",256);
    public static int accuracyNumber = 10;
    static final long serialVersionUID = -3010695769693014399L;
    
    private volatile String name;
    private volatile int rarity;
    private volatile List< Map<String, Integer> > wordsAfter;
    
    public static synchronized void setAccuracyNumber(int accuracyNumber){
        Word.accuracyNumber = accuracyNumber;
    }
    
    public Word(String name) {
        this.name = name;
        this.rarity = 1;
        this.wordsAfter = new ArrayList<>();
        
        for(int i=0; i<accuracyNumber; i++){
            wordsAfter.add(new HashMap<String, Integer>());
        }
        
    }
    
    public Word(String name, int size) {
        this.name = name;
        this.rarity = 1;
        this.wordsAfter = new ArrayList<>();
        
        for(int i=0; i<size; i++){
            wordsAfter.add(new HashMap<String, Integer>());
        }
        
    }

    public void increaseRarity() {
        rarity++;
    }

    public synchronized void addWordAfter(Word word, int index) {
        if (wordsAfter.get(index).containsKey(word)) {
            wordsAfter.get(index).put(word.getName(), wordsAfter.get(index).get(word)+1);
        } else {
            wordsAfter.get(index).put(word.getName(), 1);
        }
    }
    
    public synchronized Map<String, Integer> getWordsAfter(int index){
        return wordsAfter.get(index);
    }
    
    public synchronized String getName() {
        return name;
    }

    public synchronized int getRarity() {
        return rarity;
    }
    
    public synchronized String toString(){
        return name;
    }

}
