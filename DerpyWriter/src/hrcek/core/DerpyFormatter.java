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
 * This class contains methods used to format input and output.
 * 
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyFormatter {
    
    /**
     * This method capitalizes words.
     * 
     * @param word Word to be capitalized.
     * @return The capitalized form of the word.
     */
    public static String captializeWord(Word word){
        String strOrig = word.getName();
        String strNew = Character.toUpperCase(strOrig.charAt(0)) + strOrig.substring(1);
                
        return strNew;
    }
    
    public static String unformatText(String text){
        text = spaceColon(text);
        text = spaceComma(text);
        text = spaceExclaim(text);
        text = spacePeriods(text);
        text = spaceSemiColon(text);
        text = spaceQuestion(text);
        text = removeSpaces(text);
        text = removeNewLines(text);
        text = removeTabs(text);
        text = removeParens(text);
        text = removeQuotes(text);
        
        return text;
    }
    
    public static List<String> formatParagraphs(List<String> paragraphs){
        List<String> formattedParagraphs = new ArrayList<>();
        for(String paragraph: paragraphs){
            paragraph = unspaceColon(paragraph);
            paragraph = unspaceSemicolon(paragraph);
            paragraph = unspacePeriods(paragraph);
            paragraph = unspaceComma(paragraph);
            paragraph = unspaceExclaim(paragraph);
            paragraph = unspaceQuestion(paragraph);
            formattedParagraphs.add(paragraph);
        }
        return formattedParagraphs;
    }
    
    public static String unspacePeriods(String in){
        String out = in.replaceAll(" \\.", ".");
        return out;
    }
    
    public static String spacePeriods(String in){
        String out = in.replaceAll("\\.", " . ");
        return out;
    }
    
    public static String unspaceQuestion(String in){
        String out = in.replaceAll(" ?", "?");
        return out;
    }
    
    public static String spaceQuestion(String in){
        String out = in.replaceAll("?", " ? ");
        return out;
    }
    
    public static String unspaceComma(String in){
        String out = in.replaceAll(" \\,", ",");
        return out;
    }
    
    public static String spaceComma(String in){
        String out = in.replaceAll("\\,", " , ");
        return out;
    }
    
    public static String unspaceExclaim(String in){
        String out = in.replaceAll(" !", "!");
        return out;
    }
    
    public static String spaceExclaim(String in){
        String out = in.replaceAll("!", " ! ");
        return out;
    }
    
    public static String unspaceSemicolon(String in){
        String out = in.replaceAll(" ;", ";");
        return out;
    }
    
    public static String spaceSemiColon(String in){
        String out = in.replaceAll(";", " ; ");
        return out;
    }
    
    public static String unspaceColon(String in){
        String out = in.replaceAll(" :", ":");
        return out;
    }
    
    public static String spaceColon(String in){
        String out = in.replaceAll(":", " : ");
        return out;
    }
    
    public static String removeSpaces(String in){
        String out = in.replaceAll("  ", " ");
        out = in.replaceAll("  ", " ");
        out = in.replaceAll("  ", " ");
        out = in.replaceAll("  ", " ");
        out = in.replaceAll("  ", " ");
        return out;
    }
    
    public static String removeNewLines(String in){
        String out = in.replaceAll("\n", " ");
        out = removeSpaces(out);
        return out;
    }
    
    public static String removeTabs(String in){
        String out = in.replaceAll("\t", " ");
        out = removeSpaces(out);
        return out;
    }
    
    public static String removeParens(String in){
        String out = in.replaceAll("\\(", "");
        out = out.replaceAll("\\)", "");
        return out;
    }
    
    public static String removeQuotes(String in){
        String out = in.replaceAll("\\", "");
        return out;
    }
    
}
