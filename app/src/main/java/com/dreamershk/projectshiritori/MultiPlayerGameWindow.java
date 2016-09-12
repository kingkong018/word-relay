package com.dreamershk.projectshiritori;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.SystemMessage;

import java.io.IOException;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class MultiPlayerGameWindow extends GameWindow {
    public static boolean isMultiPlayerGameWindowRunning = false;
    boolean isServer, isConnected;

    public void setConnected(){
        isConnected = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isMultiPlayerGameWindowRunning = true;
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (intent.getExtras().getBoolean("server")) {
            isServer=true;
            final GameView g = this;
            final Context c = getApplicationContext();
            //addMessage("加載數據庫中....");
            //addMessage("加載完之後其他人先入得房, 請耐心等待..");
            (new Thread(new Runnable(){
                @Override
                public void run() {
                    MultiPlayerGameManager.releaseGameManagerInstance();
                    MultiPlayerGameManager gameManager = MultiPlayerGameManager.getGameManagerInstance();
                    gameManager.addGameView(g, intent.getExtras().getString("myDeviceName"));
                    gameManager.setContext(c);
                    gameManager.startServer();
                }
            })).start();
        }
        else {
            try {
                String host = intent.getExtras().getString(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_ADDRESS);
                Log.i(log_name, "receiver service received " + host);
                int port = intent.getExtras().getInt(MultiPlayerGameManager.EXTRAS_GROUP_OWNER_PORT);
                Log.i(log_name, "receiver service received " + port);
                String myName = intent.getExtras().getString("myDeviceName");
                setGameActionListener(new RemoteGameActionHandler(host, port, this, myName));
            } catch (IOException e) {
                Log.i(log_name, "Error connecting to specified host and port.");
                finish();
            }
        }
    }

    @Override
    public void addMessage(CirculateMessage message) {
        super.addMessage(message);
        final CirculateMessage circulateMessage = message;
        final String m = circulateMessage.getMessage();
        if (m.equals(SystemMessage.CANNOT_CONNECT_TO_HOST)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!isServer && m.equals(SystemMessage.HOST_LEFT) && !isOver){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cleanUp();
                    new AlertDialog.Builder(MultiPlayerGameWindow.this)
                            .setTitle("遊戲結束")
                            .setMessage("房主走左佬!下次再玩過啦!")
                            .setCancelable(false)
                            .setPositiveButton("拜拜", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            });
        }
        if (isServer && m.equals(SystemMessage.HOST_ROOM_INITIALIZATION_FAILURE)){
            Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        isMultiPlayerGameWindowRunning = false;
        super.onDestroy();
        cleanUp();
        Log.i(log_name, "onDestroy() called");
        if (isOver){
            if (isServer)
                MultiPlayerGameManager.getGameManagerInstance().closeServer();
        } else {
            if (isServer)
                MultiPlayerGameManager.releaseGameManagerInstance();
            else if (isConnected) {
                if (gameActionListener != null)
                    gameActionListener.windowClosed();
            }
        }
    }
}
