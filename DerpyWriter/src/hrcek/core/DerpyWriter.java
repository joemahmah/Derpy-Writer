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
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyWriter {

    private volatile Dictionary dictionary;
    private int targetSentencesPerParagraph;

    private static boolean ignorePunctuation = false;

    public static void setIgnorePunctuation(boolean ignorePunctuation) {
        DerpyWriter.ignorePunctuation = ignorePunctuation;
    }

    public static boolean ignoresPunctuation() {
        return ignorePunctuation;
    }

    public DerpyWriter(Dictionary dictionary) {
        this.dictionary = dictionary;
        targetSentencesPerParagraph = 5;
    }

    /**
     * Set the amount of sentences in a paragraph
     * @param targetSentencesPerParagraph 
     */
    public void setTargetSentencesPerParagraph(int targetSentencesPerParagraph) {
        this.targetSentencesPerParagraph = targetSentencesPerParagraph;
    }

    /**
     * Method that generates a defined length of words
     * 
     * @param wordCount the number of words you want to be in a story
     * @return A list of strings that contain the generated story
     */
    List<String> generateStory(int wordCount) {
        List<String> story = new ArrayList<>();
        int sentenceCount = 0;
        Word[] lastWords = new Word[Word.accuracyNumber];
        String paragraph = "";

        for (int i = 0; i < lastWords.length; i++) {
            lastWords[i] = LogicFactory.getRandomWord(dictionary);
        }

        boolean isFirstParagraph = true;
        for (int i = 0; i < wordCount; i++) {
            Word lastWord;

            lastWord = LogicFactory.getRandomWord(lastWords, dictionary);

            if (!isFirstParagraph) {
                if (lastWords.length > 1 && DerpyReader.isEndPunctuation(lastWords[1])) {
                    paragraph += DerpyFormatter.captializeWord(lastWord) + " ";
                } else {
                    paragraph += lastWord.getName() + " ";
                }
                
                if(DerpyReader.isPunctuation(lastWord)){
                    i--; //Punctuation no longer counts as a word in the wordcount.
                }
            } else{
                i--; //Decrements so that the first paragraph does not add to word count.
            }

            if (DerpyReader.isEndPunctuation(lastWord)) {
                sentenceCount++;
            }

            if (sentenceCount >= targetSentencesPerParagraph) {
                sentenceCount = 0;

                //Add "paragraph" to story
                if (!isFirstParagraph) {
                    story.add(paragraph.toString());
                    paragraph = "";
                    
                    if(Boot.VERBOSE){
                        System.out.println("Wrote Paragraph " + story.size() + "...");
                    }
                } else {
                    isFirstParagraph = false;
                    paragraph = "";
                }
            }
        }

        story.add(paragraph); //Adds remaining paragraph

        return story;
    }

}
