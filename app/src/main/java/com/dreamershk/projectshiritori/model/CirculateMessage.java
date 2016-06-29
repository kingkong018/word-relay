package com.dreamershk.projectshiritori.model;

/**
 * Created by Windows7 on 28/6/2016.
 */
public class CirculateMessage {
    public String author, message, word;
    public void setAuthor(String author){
        this.author = author;
    }
    public void setMessage (String message){
        this.message = message;
    }
    public void setWord (String word){
        this.word = word;
    }
    public String getAuthor(){
        return author;
    }
    public String getMessage(){
        return message;
    }
    public String getWord(){
        return word;
    }
}
