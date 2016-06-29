package com.dreamershk.projectshiritori;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.SystemMessage;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class MultiPlayerGameWindow extends GameWindow {
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
}
