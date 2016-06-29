package com.dreamershk.projectshiritori.model;

import com.dreamershk.projectshiritori.GameView;

/**
 * Created by Kong on 4/6/2016.
 */
public class Player {
    private GameView gameView;
    private String name;
    private int score, chance, round;
    public Player(){

    }
    public Player(String name, int round, int score){
        this.name = name;
        this.round = round;
        this.score = score;
    }
    public Player(GameView gameView){
        name="玩家";
        score = 0;
        round = 0;
        chance = 3;
        this.gameView = gameView;
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
    public String getName(){
        return name;
    }
    public void setName(String s){
        name =s;
    }
    public int getScore(){
        return score;
    }
    public void scoreIncrement(int i){
        score+=i;
    }
    public void roundIncrement(){round++;}
    public int getRound(){return round;}
    public void chanceDecrement(){
        chance--;
    }
    public void chanceIncrement(){
        chanceIncrement(1);
    }
    public void chanceIncrement(int i){
        if (chance < 3)
            chance+=i;
    }
    public int getChance(){ return chance; }
    public GameView getGameView(){
        return gameView;
    }
    public boolean isPlaying(){
        return gameView != null;
    }
    public void close(){
        gameView = null;
        chance = 0;
    }
}
