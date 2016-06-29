package com.dreamershk.projectshiritori;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class SinglePlayerGameWindow extends GameWindow {

    private SinglePlayerGameManager gameManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GameView g = this;
        final Context c = getApplicationContext();
        SinglePlayerGameManager.releaseGameManagerInstance();
        gameManager = SinglePlayerGameManager.getGameManagerInstance();
        gameManager.addGameView(g, "Player1");
        //context should be set after host player is in the player queue.
        gameManager.setContext(c);
        gameManager.addGameView(null, "電腦"); //add computer player
        /*new AsyncTask<Void, Void, String>(){
            @Override
            protected void onPreExecute() {
                //Add loading screen?
                loadingDialog = new ProgressDialog(SinglePlayerGameWindow.this);
                loadingDialog.setMessage("Loading...");
                loadingDialog.setCancelable(false);
                loadingDialog.setInverseBackgroundForced(false);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                gameManager.addGameView(g, "Player1");
                //context should be set after host player is in the player queue.
                gameManager.setContext(c);
                gameManager.addGameView(null, "電腦"); //add computer player
                return null;
            }
        }.execute();*/
        //after adding the computer player, the game starts.
    }
}
