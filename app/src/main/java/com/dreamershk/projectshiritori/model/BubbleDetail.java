package com.dreamershk.projectshiritori.model;

/**
 * Created by Kong on 4/6/2016.
 */
public class BubbleDetail {
    UserType type;
    String author, message;
    int life;

    public BubbleDetail(UserType type, String author, String message, int life){
        this.author = author;
        this.message = message;
        this.life = life;
        this.type = type;
    }

    public void setAuthor(String author){
        this.author = author;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setLife(int life){
        this.life = life;
    }
    public void setUserType(UserType type){
        this.type = type;
    }
    public String getAuthor(){
        return author;
    }
    public String getMessage(){
        return message;
    }
    public int getLife(){
        return life;
    }
    public UserType getUserType(){
        return type;
    }
}
