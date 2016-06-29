package com.dreamershk.projectshiritori;

/**
 * Created by Windows7 on 25/6/2016.
 */
public interface GameActionListener {
    public void startGame();
    public void wordSubmitted(String input);
    public void abstain();
    public void windowClosed();
    public void myNameIs(String name);
}
