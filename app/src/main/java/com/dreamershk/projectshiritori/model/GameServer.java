package com.dreamershk.projectshiritori.model;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Windows7 on 25/6/2016.
 */
public class GameServer extends Thread{
    private String log_name = "GAMESERVER";
    private ServerSocket serverSocket;
    public GameServer() throws IOException {
        serverSocket = new ServerSocket(8988);
    }
    public void run() {
        try {
            while(true) {
                Socket socket;
                socket = serverSocket.accept();
                Log.i(log_name, "serverSocket.accept() success");

                /***********************/
                //addGameView(new RemoteGameWindow(socket));

                /**********************/
            }
        } catch (IOException e) {
            System.err.println("Error establishing new connection");
        }
    }
    public void end() throws IOException{
        if (serverSocket!=null)
            serverSocket.close();
    }
}
