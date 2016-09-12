package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class SinglePlayerGameManager extends GameManager {
    public static SinglePlayerGameManager getGameManagerInstance() {
        if (instance == null)
            instance = new SinglePlayerGameManager();
        return (SinglePlayerGameManager)instance;
    }

    @Override
    public synchronized void startGame() {
        if (playerQueue.size() > 1) {
            currentPlayer = null;
            isStarted = true;
            //swap player and computer ai in the queue list, to let computer play first
            Player player = playerQueue.remove(0);
            playerQueue.add(1, player);
            //
            nextTurn();
        }
    }

    @Override
    public synchronized void startTurn() {
        if(currentPlayer != null) {
            if (currentPlayer.getPlayerType() == Player.TYPE_COMPUTER) {
                answer = ComputerAI.playByComputer(history, lastValidWord, currentPlayer.getScore());
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){

                }
                endTurn();
            } else{
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().activate();
            }
        }
    }


    @Override
    public synchronized void checkBonus() {
        double targetScore = Math.pow(2, currentPlayer.getLevel());
        boolean isScoreAchieved = currentPlayer.getScore() >= targetScore;
        boolean isRoundAchieved = currentPlayer.getRound()%15 == 0;
        if (isScoreAchieved || isRoundAchieved){
            CirculateMessage circulateMessage = new CirculateMessage();
            circulateMessage.setAuthor(currentPlayer.getName());
            circulateMessage.setPlayer(currentPlayer);
            if (currentPlayer.getChance() < 3){
                currentPlayer.chanceIncrement();
                circulateMessage.setMessage(SystemMessage.EXTRA_CHANCE);
            }else{
                int i = (int)(Math.random()*2);
                switch (i){
                    case 0:
                        currentPlayer.remainingSkipTimeIncrement();
                        break;
                    case 1:
                        currentPlayer.remainingHelpTimeIncrement();
                        break;
                    default:
                        break;
                }
                circulateMessage.setMessage(SystemMessage.EXTRA_SKILL);
            }
            if (isScoreAchieved){
                circulateMessage.setExtra(SystemMessage.SCORE_ACHIEVED);
                sentSystemMessage2AllPlayers(circulateMessage);
                currentPlayer.levelUp();
            }
            if (isRoundAchieved) {
                circulateMessage.setExtra(SystemMessage.ROUND_ACHIEVED);
                sentSystemMessage2AllPlayers(circulateMessage);
            }
        }
    }

    private String giveARandomWord(){
        return GameRulesHandler.giveARandomWord();
    }
    private void playByComputer() {
        int i;
        //ramdomly search for a suitable answer
        String word = "";
        if (history.size() == 0) {
            word = giveARandomWord();
        }else {
            i = (int)(Math.random() * 100);
            //the AI will send the answer according to probability;
            if (i < 10){
                word = GameRulesHandler.giveARandomWord();
            }else{
                List<String> list;
                do {
                    //Use lastValidWord instead of answer because answer may be incorrect.
                    list = GameRulesHandler.giveAllValidAnswers(lastValidWord.substring(lastValidWord.length() - 1));
                    i = (int)(Math.random() * list.size());
                    word = list.get(i);
                }while (GameRulesHandler.isExist(history, word));
            }
        }
        answer = word;
        endTurn();
    }
}
