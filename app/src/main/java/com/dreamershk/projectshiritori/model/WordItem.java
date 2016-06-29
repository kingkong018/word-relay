package com.dreamershk.projectshiritori.model;

/**
 * Created by Windows7 on 27/6/2016.
 */
public class WordItem {
    private String firstChar, lastChar, word;
    int difficulty, wordCount;
    long ID;
    public WordItem (long ID, String firstChar, int wordCount, int difficulty){
        this(firstChar,wordCount,difficulty);
        this.ID = ID;
    }
    public WordItem (String firstChar, int wordCount, int difficulty){
        this.firstChar = firstChar;
        this.wordCount = wordCount;
        this.difficulty = difficulty;
    }
    public WordItem (long ID, String word, String firstChar, String lastChar, int difficulty){
        this(word, firstChar, lastChar, difficulty);
        this.ID = ID;
    }
    public WordItem (String word, String firstChar, String lastChar, int difficulty){
        this.word = word;
        this.firstChar = firstChar;
        this.lastChar = lastChar;
        this.difficulty = difficulty;
    }
    public void setID(long ID){this.ID = ID;}
    public long getID(){return ID;}
    public String getFirstChar(){return firstChar;}
    public String getLastChar(){return lastChar;}
    public String getWord(){return word;}
    public int getDifficulty(){return difficulty;}
    public int getWordCount(){return wordCount;}
}
