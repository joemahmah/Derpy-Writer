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
 * This class is the derpy representation of a word. Each word contains information
 * about the words which follow (up to a global accuracy number). The words following
 * are stored as strings which means that they can theoretically be used in a
 * cross-dictionary manner. The global accuracy attribute can cause trouble if
 * used incorrectly...
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
    
    /**
     * This method is used to set the global accuracy value. Use this method with
     * care! It can break the program if it is used to change the accuracy beyond
     * what any words may contain...
     * 
     * @param accuracyNumber Higher accuracy == Less derp?
     */
    public static synchronized void setAccuracyNumber(int accuracyNumber){
        Word.accuracyNumber = accuracyNumber;
    }
    
    /**
     * Standard constructor for the word object. This should be used in most
     * cases.
     * 
     * @param name The string representation of the word.
     */
    public Word(String name) {
        this.name = name;
        this.rarity = 1;
        this.wordsAfter = new ArrayList<>();
        
        for(int i=0; i<accuracyNumber; i++){
            wordsAfter.add(new HashMap<String, Integer>());
        }
        
    }
    
    /**
     * This is an additional constructor for the word object. It may be used as
     * a way to create words with custom accuracy.
     * 
     * @param name The string representation of the word.
     * @param size The accuracy of the string.
     */
    public Word(String name, int size) {
        this.name = name;
        this.rarity = 1;
        this.wordsAfter = new ArrayList<>();
        
        for(int i=0; i<size; i++){
            wordsAfter.add(new HashMap<String, Integer>());
        }
        
    }

    /**
     * This increases the rarity value of the word.
     * 
     * @deprecated This is currently unused...
     */
    public void increaseRarity() {
        rarity++;
    }

    /**
     * This method adds words to the current word's list of words that follow.
     * 
     * @param word Word to be added.
     * @param index This is how far back the word occurred.
     */
    public synchronized void addWordAfter(Word word, int index) {
        if (wordsAfter.get(index).containsKey(word)) {
            wordsAfter.get(index).put(word.getName(), wordsAfter.get(index).get(word)+1);
        } else {
            wordsAfter.get(index).put(word.getName(), 1);
        }
    }
    
    /**
     * This gets a map containing the string representations of the words that
     * follow this one index words away.
     * 
     * @param index This is the number away the occurrence is.
     * @return A map containing string representations of the words that followed this one.
     */
    public synchronized Map<String, Integer> getWordsAfter(int index){
        return wordsAfter.get(index);
    }
    
    /**
     * This method gets the string representation of the word. This is used when
     * printing text.
     * 
     * @return The string representation of the word.
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * This method gets the rarity of this word.
     * 
     * @deprecated Not used...
     * 
     * @return The rarity of the word.
     */
    public synchronized int getRarity() {
        return rarity;
    }
    
    /**
     * The toString() method... It does what every other toString() method does...
     * 
     * @return The string representation of the word.
     */
    public synchronized String toString(){
        return name;
    }

}
