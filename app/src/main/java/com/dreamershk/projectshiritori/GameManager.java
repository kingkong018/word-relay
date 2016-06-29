package com.dreamershk.projectshiritori;

import android.content.Context;
import android.util.Log;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Windows7 on 25/6/2016.
 */
public class GameManager {
    private String SYSTEM_TAG = "this is the system administrator, my name is system administrator";
    protected String log_name = "GAMEMANAGER";
    //
    protected static GameManager instance;
    protected Context context;
    //
    protected ArrayList<Player> playerList;
    protected ArrayList<Player> playerQueue;
    protected Player currentPlayer, lastWonPlayer;
    //
    protected String answer, lastValidWord;
    //
    //ArrayList<String> database;
    ArrayList<String> history;
    //
    boolean isStarted, isOver, isLastWonPlayerLostInThisTurn;

    public GameManager() {
        playerList = new ArrayList<Player>();
        playerQueue = new ArrayList<Player>();
        //database = new ArrayList<String>();
        history = new ArrayList<String>();
    }
    public static GameManager getGameManagerInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }
    public static void releaseGameManagerInstance() {
        if (instance != null) instance.closeAllWindow();
        instance = null;
    }

    public synchronized void setContext(Context context) {
        this.context = context;
        //Set up database
        /*database = Database.getDatabase(context);
        if (database.isEmpty()) {
            if (playerList != null && playerList.get(0) != null && playerList.get(0).getGameView() != null)
                playerList.get(0).getGameView().addMessage("資料庫載入失敗，請重開遊戲。");
        } else if (playerList != null && playerList.get(0) != null && playerList.get(0).getGameView() != null)
            playerList.get(0).getGameView().addMessage("成功載入資料庫。");
            */
    }
    public synchronized void addGameView(GameView gameView) {
        this.addGameView(gameView, "佚名");
    }
    public synchronized void addGameView(GameView gameView, String author) {
        //gameView.setDisplayBar(word_list_on_game);
        Player player = new Player(gameView, author);
        if (gameView != null)
            gameView.setGameActionListener(new GameActionHandler(player));
        playerList.add(player);
        //setPlayerQueueBar(playerList, null, null);
        playerQueue.add(player);
        Log.i(log_name, "playerQueue.size()=" + playerQueue.size());
        if (player.equals(playerList.get(0))){
            player.getGameView().setStartGame();
        }
        /*if (playerList.size() >= 2) {
            //game starts
            if (!isStarted)
                startGame();
        }*/
    }
    public synchronized void startGame() {
        if (playerQueue.size() > 1) {
            //broadcast("Game started!");
            currentPlayer = null;
            isStarted = true;
            nextTurn();
        }
    }
    private class GameActionHandler implements GameActionListener {
        private Player player;
        public GameActionHandler(Player player) {
            this.player = player;
        }
        //receive messages from gameWindow
        public void wordSubmitted(String input) {
            answer = input;
            endTurn();
        }
        public void abstain() {
            answer = SystemMessage.DRAWN;
            endTurn();
        }
        public void windowClosed() {
            if (player.getGameView() != null)
                closeGameView(player);
        }
        public void myNameIs(String name) {
            player.setName(name);
        }
        public void startGame(){
            GameManager.this.startGame();
        }
}

    //overidden required in Multi-Game
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
            StringBuffer result = new StringBuffer();
            // result.append("=== Result ==="+System.lineSeparator());
            for (Player player : playerList) {
                result.append(player.getName() + "\t" + player.getRound() + "\t" + player.getScore() + "\n");
            }
            for (Player player : playerList)
                if (player.getGameView() != null) {
                    //player.getGameView().addMessage("Game ends");
                    player.getGameView().showResult(result.toString());
                }
            /**
             * For Multi-Game only
             * closeServer();
             */
        } else {
            pickNextPlayer();
            startTurn();
        }
    }
    public synchronized void prepareNextTurn() {
        //
    }
    protected void sendMessage(Player player, CirculateMessage message) {
        if (player != null && player.getGameView() != null)
            player.getGameView().addMessage(message);
    }
    protected void addChatBubble(String author, String word) {
        this.addChatBubble(author, word, -1);
    }
    protected void addChatBubble(String author, String word, int life) {
        for (Player p : playerList) {
            if (p != null && p.getGameView() != null) {
                if (p.getName().equals(author)) {
                    p.getGameView().addChatBubble(UserType.SELF, author, word, life);
                } else if (author.equals(SYSTEM_TAG)) {
                    p.getGameView().addChatBubble(UserType.SYSTEM, author, word, life);
                } else
                    p.getGameView().addChatBubble(UserType.OTHER, author, word, life);
            }
        }
    }
    private void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer){
        for (Player p : playerList){
            if (p!=null && p.getGameView()!=null)
                p.getGameView().setPlayerQueueBar(playerList, currentPlayer, leftPlayer);
        }
    }
    private synchronized boolean isGameOver() {
        int numberOfTheAlive=0;
        for (Player player : playerList)
            if (player.getChance() > 0)
                numberOfTheAlive++;
        if (numberOfTheAlive > 1)
            return false;
        return true;
    }
    public synchronized void pickNextPlayer() {
        if(currentPlayer != null) {
            playerQueue.add(currentPlayer);
        }
        currentPlayer = playerQueue.remove(0);
        setPlayerQueueBar(playerList, currentPlayer, null);
        if(currentPlayer.getChance() <= 0)
            pickNextPlayer();
    }
    public synchronized void startTurn() {
        //
    }
    public synchronized void endTurn() {
        if(currentPlayer != null && currentPlayer.getGameView() != null) {
            currentPlayer.getGameView().deactivate();
        }
        scheduleNextTurn();
    }
    /**
     * Start next turn after 0.5s
     */
    public synchronized void scheduleNextTurn() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                nextTurn();
            }
        }.start();
    }
    /**
     * Close a game window. End the current turn if the game window is the current one
     * Terminate the program if all windows are closed
     * @param //gameWindow The gameWindow to be closed
     */
    public synchronized void closeGameView(Player player) {
        //called when a user leaves his game window
        player.close();
        if (playerList.get(0).getGameView() == null)
            return;
        setPlayerQueueBar(playerList, null, player);
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessage(player.getName() + SystemMessage.PLAYER_DISCONNECTED);
        for (Player p : playerList)
            sendMessage(p, circulateMessage);
        if(currentPlayer == player) {
            playerQueue.remove(player);
            currentPlayer = null;
            endTurn();
        } else {
            playerQueue.remove(player);
        }
        Log.i(log_name, "playerQueue.size()="+playerQueue.size());
        if(playerQueue.size() == 0 && currentPlayer == null) {
            GameManager.releaseGameManagerInstance();
        }
        else if (playerQueue.size() == 0){
            currentPlayer = null;
            nextTurn();
        }
    }
    public synchronized void closeAllWindow() {
        if (!isOver) {
            //called when the host leaves
            closeGameView(playerList.get(0));
            CirculateMessage circulateMessage = new CirculateMessage();
            circulateMessage.setMessage(SystemMessage.HOST_LEFT);
            for (Player player : playerList)
                sendMessage(player, circulateMessage);
            //closeServer();
        }
    }
}
