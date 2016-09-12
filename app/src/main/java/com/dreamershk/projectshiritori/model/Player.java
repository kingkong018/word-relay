package com.dreamershk.projectshiritori.model;

import android.media.Image;
import android.widget.ImageView;

import com.dreamershk.projectshiritori.GameView;

/**
 * Created by Kong on 4/6/2016.
 */
public class Player {
    public static int TYPE_COMPUTER = 1000;
    public static int TYPE_HUMAN = 1001;

    private GameView gameView;
    private String name;
    private int playerType, score, chance, round, level, remainingSkipTime, remainingReverseTime, remainingHelpTime, remainingAssignTime, iconResId;
    public Player(){

    }
    public Player(String name, int round, int score){
        this.name = name;
        this.round = round;
        this.score = score;
    }
    public Player(String name, int round, int score, int chance){
        this.name = name;
        this.round = round;
        this.score = score;
        this.chance = chance;
    }
    public Player(GameView gameView){
        name="玩家";
        score = 0;
        round = 0;
        chance = 3;
        level = 2;
        this.gameView = gameView;
        remainingAssignTime = 0;
        remainingHelpTime = 0;
        remainingReverseTime = 0;
        remainingSkipTime = 0;
        //playerlist.add(this);
    }
    public Player(GameView gameView, String name){
        this(gameView);
        this.name = name;
    }
    public Player(GameView gameView, String name, int round, int score){
        this(gameView);
        this.name = name;
        this.round = round;
        this.score = score;
    }
    public synchronized String getName(){
        return name;
    }
    public synchronized void setName(String s){
        name =s;
    }
    public synchronized int getScore(){
        return score;
    }
    public synchronized void scoreIncrement(int i){
        score+=i;
    }
    public synchronized void scoreDecrement(int i) {score -= i;}
    public synchronized void roundIncrement(){round++;}
    public synchronized void remainingAssignTimeIncrement(){
        remainingAssignTime++;
    }
    public synchronized void remainingHelpTimeIncrement(){remainingHelpTime++;}
    public synchronized void remainingReverseTimeIncrement(){
        remainingReverseTime++;
    }
    public synchronized void remainingSkipTimeIncrement(){remainingSkipTime++;}
    public synchronized void remainingAssignTimeDecrement(){
        remainingAssignTime--;
    }
    public synchronized void remainingHelpTimeDecrement(){remainingHelpTime--;}
    public synchronized void remainingReverseTimeDecrement(){
        remainingReverseTime--;
    }
    public synchronized void remainingSkipTimeDecrement(){remainingSkipTime--;}
    public void setIconResId(int resId){iconResId = resId;}
    public int getIconResId(){return iconResId;}
    public void setPlayerType(int type){this.playerType = type;}
    public int getPlayerType(){return playerType;}
    public synchronized int getRound(){return round;}
    public synchronized int getLevel(){return level;}
    public synchronized void levelUp(){level++;}
    public synchronized void chanceDecrement(){
        chance--;
    }
    public synchronized void chanceIncrement(){
        chanceIncrement(1);
    }
    public synchronized void chanceIncrement(int i){
        if (chance < 3)
            chance+=i;
    }
    public synchronized int getChance(){ return chance; }
    public synchronized GameView getGameView(){
        return gameView;
    }
    public synchronized int getRemainingSkipTime(){return remainingSkipTime;}
    public synchronized int getRemainingReverseTime(){return remainingReverseTime;}
    public synchronized int getRemainingHelpTime(){return remainingHelpTime;}
    public synchronized int getRemainingAssignTime(){return remainingAssignTime;}
    public synchronized boolean isPlaying(){
        return gameView != null;
    }
    public synchronized void close(){
        gameView = null;
        chance = 0;
    }
}
