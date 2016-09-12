package com.dreamershk.projectshiritori.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Windows7 on 28/6/2016.
 */
public class CirculateMessage implements Serializable{
    private static final long serialVersionUID = 1L;

    private int messageType;
    private String author, message, word, extra;
    private int score, chance, round, iconResId;
    private Player player;
    private CirculateMessage circulateMessage;
    private UserType userType;
    private List<Player> playerQueue;

    public void setAuthor(String author){
        this.author = author;
    }
    public void setMessage (String message){
        this.message = message;
    }
    public void setWord (String word){
        this.word = word;
    }
    public void setExtra (String extra){
        this.extra = extra;
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public void setMessageType(int messageType){this.messageType = messageType;}
    public void setCirculateMessage(CirculateMessage circulateMessage){this.circulateMessage = circulateMessage;}
    public void setScore(int score){this.score = score;}
    public void setChance(int chance){this.chance = chance;}
    public void setRound(int round){this.round = round;}
    public void setUserType(UserType userType){this.userType = userType;}
    public void setPlayerQueue(List<Player> playerQueue){this.playerQueue = playerQueue;}
    public void setIconResId(int iconResId){this.iconResId = iconResId;}
    public List<Player> getPlayerQueue(){return playerQueue;}
    public UserType getUserType(){return userType;}
    public int getScore(){return score;}
    public int getChance(){return chance;}
    public int getRound(){return round;}
    public CirculateMessage getCirculateMessage(){return circulateMessage;}
    public int getMessageType(){return messageType;}
    public String getAuthor(){
        return author;
    }
    public String getMessage(){
        return message;
    }
    public String getWord(){
        return word;
    }
    public String getExtra(){
        return extra;
    }
    public Player getPlayer(){
        return player;
    }
    public int getIconResId(){return iconResId;}
}
