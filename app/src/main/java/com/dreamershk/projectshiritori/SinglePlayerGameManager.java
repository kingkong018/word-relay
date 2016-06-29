package com.dreamershk.projectshiritori;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.WordItem;

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
            playerQueue.add(player);
            nextTurn();
        }
    }

    @Override
    public synchronized void startTurn() {
        if(currentPlayer != null) {
            if (currentPlayer.getName().equals("電腦")) {
                playByComputer();
            } else{
                if (currentPlayer.getGameView() != null) {
                    //Change the UI..
                    for (Player player : playerList){
                        if (player!=currentPlayer){
                            //Change the UI..
                        }
                    }
                }
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().activate();
            }
        }
    }

    @Override
    public synchronized void prepareNextTurn() {
        if (currentPlayer == null)
            return;
        if (answer != null && !answer.equals("")) {
            boolean isCorrect = false;
            boolean isDrawn = answer.equals(SystemMessage.DRAWN);
            boolean isAWord = GameRulesHandler.isAWord(answer);
            boolean isExist = GameRulesHandler.isExist(history, answer);
            boolean isValid = GameRulesHandler.isValid(history, answer);
            if (isAWord && !isExist && isValid)
                isCorrect = true;

            String author = currentPlayer.getName();
            int life = currentPlayer.getChance();
            if (isCorrect) {
                //lastWonPlayer = currentPlayer;
                //isLastWonPlayerLostInThisTurn = false;
                currentPlayer.scoreIncrement(answer.length());
                currentPlayer.roundIncrement();
                addChatBubble(author, answer, life);
                //regularChanceBonus();
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                history.add(answer);
                lastValidWord = answer;
                //Check is it impossible to answer in the next round
                String word = answer;
                List<String> list;
                boolean isPossible2Answer = false, isNewWordGenerated = false;
                 do{
                     list = GameRulesHandler.giveAllValidAnswers(word.substring(word.length()-1));
                     for (int i = 0; i<list.size(); i++){
                        if (list.size() == 1 && list.get(0).equals(SystemMessage.NO_VALID_WORD_IN_DATABASE)){ //if there is no valid word, the handler return a error message.
                            break;
                        }else if (!GameRulesHandler.isExist(history, list.get(i))){
                            isPossible2Answer = true;
                            break;
                        }
                    }
                    if(!isPossible2Answer){
                        isNewWordGenerated = true;
                        do{
                            word = GameRulesHandler.giveARandomWord();
                        }while(GameRulesHandler.isExist(history,word));
                    }
                }while(!isPossible2Answer);
                if (isNewWordGenerated){
                    answer = word;
                    lastValidWord = answer;
                    history.add(answer);
                    //notify all users
                    CirculateMessage circulateMessage = new CirculateMessage();
                    circulateMessage.setMessage(SystemMessage.NOT_POSSIBLE_TO_ANSWER);
                    circulateMessage.setWord(answer);
                    for (Player player : playerList){
                        sendMessage(player, circulateMessage);
                    }
                }
                for (Player player : playerList)
                    if (player.getGameView() != null)
                        player.getGameView().setLastChar(answer.substring(answer.length()-1));
            } else {
                CirculateMessage circulateMessage = new CirculateMessage();
                circulateMessage.setAuthor(author);
                currentPlayer.chanceDecrement();
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                if (isDrawn) {
                    circulateMessage.setMessage(SystemMessage.DRAWN);
                } else {
                    addChatBubble(author, answer, life);
                    if (!isAWord) {
                        circulateMessage.setMessage(SystemMessage.WORD_DOES_NOT_EXIST);
                    } else if (isExist) {
                        circulateMessage.setMessage(SystemMessage.WORD_REPEATED);
                    } else if (!isValid) {
                        circulateMessage.setMessage(SystemMessage.WORD_IS_INVALID);
                    }
                }
                for (Player p : playerList){
                    sendMessage(p, circulateMessage);
                }
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
            List<String> list;
            do {
                //Use lastValidWord instead of answer because answer may be incorrect.
                list = GameRulesHandler.giveAllValidAnswers(lastValidWord.substring(lastValidWord.length() - 1));
                if (list.get(0).equals(SystemMessage.NO_VALID_WORD_IN_DATABASE)){ //check list of words exist or not..
                    break;
                }
                i = (int)(Math.random() * list.size());
                word = list.get(i);
            }while (GameRulesHandler.isExist(history, word));
        }
        /*Randomly pick a valid word if no suitable answer is found.
        if (!isWordFound) {
            boolean isExist = true;
            while(isExist){
                word = giveARandomWord();
                if (GameRulesHandler.isExist(history,answer)){
                    isExist = false;
                }
            }
        }*/
        //the AI will send the answer according to probability;
        if (!word.equals(SystemMessage.NO_VALID_WORD_IN_DATABASE)){
            i = (int)(Math.random() * 100);
            if (i < 20) answer ="AI放棄";
        }
        answer = word;
        endTurn();
    }
}
