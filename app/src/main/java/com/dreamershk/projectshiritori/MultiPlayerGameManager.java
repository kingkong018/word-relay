package com.dreamershk.projectshiritori;

import android.util.Log;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class MultiPlayerGameManager extends GameManager{

    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public static MultiPlayerGameManager getGameManagerInstance() {
        if (instance == null)
            instance = new MultiPlayerGameManager();
        return (MultiPlayerGameManager)instance;
    }

    private GameServer gameServer;
    /**
     * Start a game server
     */
    public void startServer() {
        try {
            gameServer = new GameServer();
            gameServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting server");
            CirculateMessage circulateMessage = new CirculateMessage();
            circulateMessage.setMessage("Host failed to initialize.");
            if (playerList.get(0)!=null) playerList.get(0).getGameView().addMessage(circulateMessage);
        }
    }

    public void closeServer() {
        try {
            if (gameServer != null) {
                gameServer.end();
                gameServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error ending server");
        }
    }

    private class GameServer extends Thread {
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
                    addGameView(new RemoteGameWindow(socket));
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

    @Override
    public synchronized void startTurn() {
        if(currentPlayer != null) {
            if (currentPlayer.getGameView() != null)
                currentPlayer.getGameView().activate();
        }
    }

    @Override
    public synchronized void checkBonus() {

    }

    public synchronized void closeAllWindow() {
        if (!isOver) {
            //called when the host leaves
            closeGameView(playerList.get(0));
            CirculateMessage circulateMessage = new CirculateMessage();
            circulateMessage.setMessage(SystemMessage.HOST_LEFT);
            for (Player player : playerList)
                sendMessage(player, circulateMessage);
            closeServer();
        }
    }
    public synchronized void nextTurn() {
        prepareNextTurn();
        if (isGameOver()) {
            //show result
            isOver = true;
            Collections.sort(playerList, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    return p2.getScore() - p1.getScore();
                }
            });
            for (Player player : playerList)
                if (player.getGameView() != null) {
                    //player.getGameView().addMessage("Game ends");
                    player.getGameView().showResult(playerList);
                }
            //For Multi-Game only
            closeServer();
        } else {
            pickNextPlayer();
            startTurn();
        }
    }
}
