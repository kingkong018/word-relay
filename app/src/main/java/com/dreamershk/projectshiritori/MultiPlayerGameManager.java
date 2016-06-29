package com.dreamershk.projectshiritori;

import android.content.Context;
import android.util.Log;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Database;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class MultiPlayerGameManager extends GameManager{
    public static MultiPlayerGameManager getGameManagerInstance() {
        if (instance == null)
            instance = new MultiPlayerGameManager();
        return (MultiPlayerGameManager)instance;
    }


    @Override
    public synchronized void startTurn() {
        if(currentPlayer != null) {
            //broadcast("Player "+currentPlayer.getPlayerNumber()+"'s turn!");
            if (isLastWonPlayerLostInThisTurn) {
                CirculateMessage circulateMessage = new CirculateMessage();
                for (Player player : playerList){
                    if (player == currentPlayer){
                        if (player == lastWonPlayer){
                            circulateMessage.setMessage(SystemMessage.LAST_WON_PLAYER_CANNOT_ANSWER_HER_WORD);
                            sendMessage(player, circulateMessage);
                        }else{
                            circulateMessage.setMessage(SystemMessage.LAST_WON_PLAYER_CAN_GIVE_ANY_ANSWER);
                            sendMessage(player, circulateMessage);
                        }
                    }else{
                        if (player == lastWonPlayer){
                            circulateMessage.setMessage(SystemMessage.LAST_WON_PLAYER_FAIL_SO_CURRENT_PLAYER_CAN_GIVE_ANY_WORD);
                            sendMessage(player, circulateMessage);
                        }else{
                            circulateMessage.setMessage(SystemMessage.CURRENT_PLAYER_CAN_GIVE_ANY_WORD);
                            sendMessage(player, circulateMessage);
                        }
                    }
                }
            } else {
                if (currentPlayer.getGameView() != null) {
                    //Change the UI
                    for (Player player : playerList){
                        if (player!=currentPlayer){
                            //change the UI..
                        }
                    }
                }
            }
            if (currentPlayer.getGameView() != null)
                currentPlayer.getGameView().activate();
        }
    }

    @Override
    public synchronized void prepareNextTurn() {
        if (currentPlayer == null)
            return;
        if (answer != null && !answer.equals("")) {
            boolean isCorrect = false;
            boolean isDrawn = answer.equals("iGivEuPlA");
            boolean isAWord = GameRulesHandler.isAWord(answer);
            boolean isExist = GameRulesHandler.isExist(history, answer);
            boolean isValid = GameRulesHandler.isValid(history, answer);
            if (isAWord && !isExist && isValid)
                isCorrect = true;
            else if (lastWonPlayer == currentPlayer) {
                if (!isLastWonPlayerLostInThisTurn)
                    isLastWonPlayerLostInThisTurn = true;
                else {
                    answer = answer.substring(1);
                    Log.i(log_name, "answer=" + answer);
                    isAWord = GameRulesHandler.isAWord(answer);
                    isExist = GameRulesHandler.isExist(history, answer);
                    if (isAWord && !isExist)
                        isCorrect = true;
                }
            } else if (isLastWonPlayerLostInThisTurn) {
                answer = answer.substring(1);
                Log.i(log_name, "answer=" + answer);
                isAWord = GameRulesHandler.isAWord(answer);
                isExist = GameRulesHandler.isExist(history, answer);
                if (isAWord && !isExist)
                    isCorrect = true;
            }
            String author = currentPlayer.getName();
            String system_message = author + " ";
            int life = currentPlayer.getChance();
            if (isCorrect) {
                lastWonPlayer = currentPlayer;
                isLastWonPlayerLostInThisTurn = false;
                currentPlayer.scoreIncrement(answer.length());
                currentPlayer.roundIncrement();
                addChatBubble(author, answer, life);
                //regularChanceBonus();
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                history.add(answer);
            } else {
                currentPlayer.chanceDecrement();
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                if (isDrawn) {
                    addChatBubble(author, system_message + SystemMessage.DRAWN);
                } else {
                    if (!isAWord) {
                        addChatBubble(author, system_message + SystemMessage.WORD_DOES_NOT_EXIST);
                    } else if (isExist) {
                        addChatBubble(author, system_message + SystemMessage.WORD_REPEATED);
                    } else if (!isValid) {
                        addChatBubble(author, system_message + SystemMessage.WORD_IS_INVALID);
                    }
                }
            }
        }
    }
}
