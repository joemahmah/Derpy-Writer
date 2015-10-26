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

/**
 * This class is an extension of Word that is used for determining punctuation
 * logic. 
 * 
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class Punctuation extends Word{

    private List<Integer> sentenceLengths; //This might need a new data type to work with large texts. (list of [avg of 10000] + current list) / total
    private int averageSentenceLength;
    
    public static final String[] punctuations = {",",".","!","?",";"};
    public static final String[] endPunctuations = {".","!","?"};
    
    /**
     * A constructor. Does everything the Word constructor does in addition to
     * defining average sentence lengths for each punctuation type.
     * 
     * @param name String literal of the punctuation type.
     */
    public Punctuation(String name) {
        super(name);
        averageSentenceLength = 0;
        sentenceLengths = new ArrayList<>();
    }
    
    /**
     * This method calculates and gets the average sentence length for this
     * punctuation.
     * 
     * @return Average sentence length for this punctuation.
     */
    public int getAverageSentenceLength(){
        
        averageSentenceLength = 0;
        
        for(int i: sentenceLengths){
            averageSentenceLength += i;
        }
        
        averageSentenceLength = averageSentenceLength / sentenceLengths.size();
        
        return averageSentenceLength;
    }
    
    /**
     * Adds a sentence length to the punctuation. This allows the punctuation to
     * determine an average.
     * 
     * @param length Length of sentence to be added.
     */
    public void addLength(int length){
        sentenceLengths.add(length);
    }
    
}
