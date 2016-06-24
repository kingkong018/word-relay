package com.dreamershk.projectshiritori;

/**
 * Created by Kong on 4/6/2016.
 */
public class Player {
    private String name;
    private int score, chance, round;

    Player(){
        name="玩家";
        score = 0;
        round = 0;
        chance = 3;
    }
    Player(String name){
        this();
        this.name = name;
    }
    Player(String name, int round, int score){
        this();
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
    public void chanceIncrement(){chance++;}
    public void chanceIncrement(int i){chance+=i;}
    public int getChance(){ return chance; }
}
