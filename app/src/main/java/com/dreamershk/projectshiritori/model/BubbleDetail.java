package com.dreamershk.projectshiritori.model;

import com.dreamershk.projectshiritori.R;

/**
 * Created by Kong on 4/6/2016.
 */
public class BubbleDetail {
    UserType type;
    String author, message;
    int life, score, round, iconResId;

    public BubbleDetail(UserType type, String author, String message, int score, int life, int round){
        this.author = author;
        this.message = message;
        this.life = life;
        this.type = type;
        this.score = score;
        this.round = round;
        iconResId = R.drawable.default_profile_icon;
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
    public void setScore (int score){
        this.score = score;
    }
    public void setRound (int round){
        this.round = round;
    }
    public void setIconResId(int iconResId){this.iconResId = iconResId;}
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
    public int getScore(){
        return score;
    }
    public int getRound(){
        return round;
    }
    public int getIconResId(){return iconResId;}
}
