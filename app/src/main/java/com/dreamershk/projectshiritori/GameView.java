package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows7 on 25/6/2016.
 */
public interface GameView {
    public void setGameActionListener(GameActionListener gameActionListener);
    public void setStartGame();
    public void addMessage(CirculateMessage message);
    public void addChatBubble(UserType userType, String author, String message, int life);
    //public void setDisplayBar(ArrayList<String> word_list);
    public void activate();
    public void deactivate();
    public void showResult(String message);
    public void setPlayerInfo(String name, int score, int chance, int round);
    public void setLastChar(String lastChar);
    public void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer);
}
