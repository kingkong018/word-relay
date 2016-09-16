package com.dreamershk.projectshiritori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreamershk.projectshiritori.model.BubbleDetail;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows7 on 25/6/2016.
 */
public interface GameActionListener {
    public void startGame();
    public void wordSubmitted(String input);
    public void abstain();
    public void skip();
    public void help();
    public void assign(int playerHashCode);
    public void reverse();
    public void windowClosed();
    public void setPlayerName(String name);
    public int getNumberOfPlayers();
    public List<Player> getPlayerQueue();
    public void timeUp();
    public void timeAlmostUp();
    public List<String> getWordList();
    public List<String> getInvalidWordList();
}
