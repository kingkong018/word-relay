package com.dreamershk.projectshiritori;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
public abstract class GameManager {
    public static int ABSTAIN_POINT = 10;
    public static int HELP_POINT = 20;
    public static int ASSIGN_POINT = 50;
    public static int REVERSE_POINT = 30;
    public static int SKIP_POINT = 20;

    private String SYSTEM_TAG = "this is the system administrator, my name is system administrator";
    protected String log_name = "GAMEMANAGER";
    //
    protected static GameManager instance;
    //Load sound effects
    protected MediaPlayer[] mediaPlayer = new MediaPlayer[10];
    protected Context context;
    //
    protected ArrayList<Player> playerList;
    protected ArrayList<Player> playerQueue;
    protected Player currentPlayer, lastWonPlayer;
    //
    protected String answer, lastValidWord;
    //
    ArrayList<String> history;
    ArrayList<String> invalidWordHistory;
    //
    boolean isStarted, isOver, isLastWonPlayerLostInThisTurn;

    public GameManager() {
        playerList = new ArrayList<Player>();
        playerQueue = new ArrayList<Player>();
        //database = new ArrayList<String>();
        history = new ArrayList<String>();
        invalidWordHistory = new ArrayList<String>();
    }
    /*public static GameManager getGameManagerInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }*/
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
        //load sounds
        mediaPlayer[0] = MediaPlayer.create(context, R.raw.message_receieved);
        mediaPlayer[1] = MediaPlayer.create(context, R.raw.count_down_3s);
        mediaPlayer[2] = MediaPlayer.create(context, R.raw.timeout);
        mediaPlayer[3] = MediaPlayer.create(context, R.raw.game_over);
        mediaPlayer[4] = MediaPlayer.create(context, R.raw.success);
        mediaPlayer[5] = MediaPlayer.create(context, R.raw.incorrect);
    }
    public synchronized void addGameView(GameView gameView) {
        this.addGameView(gameView, "佚名");
    }
    public synchronized void addGameView(GameView gameView, String author) {
        //gameView.setDisplayBar(word_list_on_game);
        Player player = new Player(gameView, author);
        this.addGameView(gameView, player);
    }
    public synchronized void addGameView(GameView gameView, Player player){
        if (gameView != null)
            gameView.setGameActionListener(new GameActionHandler(player));
        playerList.add(player);
        //setPlayerQueueBar(playerList, null, null);
        playerQueue.add(player);
        Log.i(log_name, "playerQueue.size()=" + playerQueue.size());
        if (player.equals(playerList.get(0))){
            player.getGameView().setStartGame();
     }
    }
    public synchronized void startGame() {
        if (playerQueue.size() > 1) {
            //broadcast("Game started!");
            currentPlayer = null;
            isStarted = true;
            nextTurn();
            //play background music
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
            answer = SystemMessage.ACTION_ABSTAIN;
            endTurn();
        }

        @Override
        public void skip() {
            answer = SystemMessage.ACTION_SKIP;
            endTurn();
        }

        @Override
        public void help() {
            answer = SystemMessage.ACTION_HELP;
            endTurn();
        }

        @Override
        public void assign(int playerHashCode) {
            answer = SystemMessage.ACTION_ASSIGN + "," + playerHashCode;
            endTurn();
        }

        @Override
        public void reverse() {
            answer = SystemMessage.ACTION_REVERSE;
            endTurn();
        }

        public void windowClosed() {
            if (player.getGameView() != null)
                closeGameView(player);
        }
        public void setPlayerName(String name) {
            player.setName(name);
        }
        public void startGame(){
            GameManager.this.startGame();
        }

        @Override
        public int getNumberOfPlayers() {
            return playerList.size();
        }

        @Override
        public synchronized List<Player> getPlayerQueue() {
            return playerQueue;
        }

        @Override
        public void timeUp() {
            mediaPlayer[2].start();
            abstain();
        }

        @Override
        public void timeAlmostUp() {
            mediaPlayer[1].start();
        }

        @Override
        public List<String> getWordList() {
            return history;
        }

        @Override
        public List<String> getInvalidWordList() {
            return invalidWordHistory;
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
            for (Player player : playerList)
                if (player.getGameView() != null) {
                    player.getGameView().showResult(playerList);
                }
        } else {
            pickNextPlayer();
            startTurn();
        }
    }
    public void prepareNextTurn(){
        if (currentPlayer == null)
            return;
        if (answer != null && !answer.equals("")) {
            String author = currentPlayer.getName();
            CirculateMessage circulateMessage = new CirculateMessage();
            circulateMessage.setAuthor(author);
            circulateMessage.setWord(lastValidWord);
            boolean isCorrect = false;
            boolean isAbstained = answer.equals(SystemMessage.ACTION_ABSTAIN);
            boolean isSkipped = answer.equals(SystemMessage.ACTION_SKIP);
            boolean isHelped = answer.equals(SystemMessage.ACTION_HELP);
            boolean isReversed = answer.equals(SystemMessage.ACTION_REVERSE);
            boolean isAssigned = answer.contains(SystemMessage.ACTION_ASSIGN);
            if (isAbstained || isSkipped || isReversed || isAssigned){
                if (isAbstained) {
                    currentPlayer.chanceDecrement();
                    circulateMessage.setMessage(SystemMessage.ACTION_ABSTAIN);
                }else if (isSkipped){
                    currentPlayer.scoreDecrement(GameManager.SKIP_POINT);
                    circulateMessage.setMessage(SystemMessage.ACTION_SKIP);
                }
                else if (isReversed){
                    //reverse player queue
                    //Suppose A is answering, the queue should be B > C > D.
                    //The proper reversed queue should be D > C > B > A.
                    int queue_size = playerQueue.size();
                    for (int i = 0; i <  queue_size / 2; i++){
                        Player player = playerQueue.get(i);
                        playerQueue.set(i, playerQueue.get(queue_size - i - 1));
                        playerQueue.set(queue_size - i - 1, player);
                    }
                    currentPlayer.scoreDecrement(GameManager.REVERSE_POINT);
                    circulateMessage.setMessage(SystemMessage.ACTION_REVERSE);
                }else if (isAssigned){
                    int playerHashCode = Integer.parseInt(answer.split(",")[1]);
                    Player player = null;
                    boolean isAssignedSuccessfully = false;
                    for (Player p : playerList){
                        if (p.hashCode() == playerHashCode) {
                            player = p;
                            playerQueue.remove(p);
                            isAssignedSuccessfully = true;
                            break;
                        }
                    }
                    if (isAssignedSuccessfully){
                        playerQueue.add(0, player); // add the player to the first of the queue
                    }
                    currentPlayer.scoreDecrement(GameManager.ASSIGN_POINT);
                    circulateMessage.setExtra(player.getName());
                    circulateMessage.setMessage(SystemMessage.ACTION_ASSIGN);
                }
                if (currentPlayer.getGameView() != null)
                    currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                sentSystemMessage2AllPlayers(circulateMessage);
            }else{
                if (isHelped){
                    currentPlayer.scoreDecrement(GameManager.HELP_POINT);
                    circulateMessage.setMessage(SystemMessage.ACTION_HELP);
                    sentSystemMessage2AllPlayers(circulateMessage);
                    List<String> list = GameRulesHandler.giveAllValidAnswers(lastValidWord.substring(lastValidWord.length()-1));
                    for (int i = 0; i < list.size(); i++){
                        if (!GameRulesHandler.isExist(history, list.get(i))){
                            answer = list.get(i);
                            break;
                        }
                    }
                }
                boolean isAWord = GameRulesHandler.isAWord(answer);
                boolean isExist = GameRulesHandler.isExist(history, answer);
                boolean isValid = GameRulesHandler.isValid(history, answer);
                if (isAWord && !isExist && isValid)
                    isCorrect = true;
                if (isCorrect) {
                    //lastWonPlayer = currentPlayer;
                    //isLastWonPlayerLostInThisTurn = false;
                    if (!isHelped) currentPlayer.scoreIncrement(answer.length()*3);
                    currentPlayer.roundIncrement();
                    addChatBubble(author, answer, currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound(),currentPlayer.getIconResId());
                    //checkBonus();
                    if (currentPlayer.getGameView() != null)
                        currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                    history.add(answer);
                    lastValidWord = answer;
                    //Check is it impossible to answer in the next round
                    String word = answer;
                    List<String> list;
                    boolean isPossible2Answer = false, isNewWordGenerated = false;
                    do{
                        list = GameRulesHandler.giveAllValidAnswers(word.substring(word.length()-1));
                        for (int i = 0; i<list.size(); i++){
                            if (list.size() == 1 && list.get(0).equals(SystemMessage.NO_VALID_WORD_IN_DATABASE)){ //if there is no valid word, the handler return a error message.
                                break;
                            }else if (!GameRulesHandler.isExist(history, list.get(i))){
                                isPossible2Answer = true;
                                break;
                            }
                        }
                        if(!isPossible2Answer){
                            isNewWordGenerated = true;
                            do{
                                word = GameRulesHandler.giveARandomWord();
                            }while(GameRulesHandler.isExist(history,word));
                        }
                    }while(!isPossible2Answer);
                    if (isNewWordGenerated){
                        answer = word;
                        lastValidWord = answer;
                        history.add(answer);
                        //notify all users
                        circulateMessage.setMessage(SystemMessage.NOT_POSSIBLE_TO_ANSWER);
                        circulateMessage.setWord(answer);
                        sentSystemMessage2AllPlayers(circulateMessage);
                    }
                    for (Player player : playerList)
                        if (player.getGameView() != null)
                            player.getGameView().setLastChar(answer.substring(answer.length()-1));
                } else {
                    circulateMessage.setWord(lastValidWord);
                    currentPlayer.chanceDecrement();
                    if (currentPlayer.getGameView() != null)
                        currentPlayer.getGameView().setPlayerInfo(currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound());
                    addChatBubble(author, answer, currentPlayer.getScore(), currentPlayer.getChance(), currentPlayer.getRound(), currentPlayer.getIconResId());
                    if (!isAWord) {
                        circulateMessage.setMessage(SystemMessage.WORD_DOES_NOT_EXIST);
                        invalidWordHistory.add(answer);
                    } else if (isExist) {
                        circulateMessage.setMessage(SystemMessage.WORD_REPEATED);
                    } else if (!isValid) {
                        circulateMessage.setMessage(SystemMessage.WORD_IS_INVALID);
                    }
                    sentSystemMessage2AllPlayers(circulateMessage);
                }
            }
            /*List<Integer> actionValue = new ArrayList<>();
            actionValue.add(currentPlayer.getRemainingSkipTime()); //skip,reverse,help,assign, abstain
            actionValue.add(currentPlayer.getRemainingReverseTime());
            actionValue.add(currentPlayer.getRemainingHelpTime());
            actionValue.add(currentPlayer.getRemainingAssignTime());
            actionValue.add(currentPlayer.getChance());*/
            if (currentPlayer.getGameView() != null)
                currentPlayer.getGameView().setSkillButton(currentPlayer.getScore(), playerList.size());
        }
    };
    protected void sentSystemMessage2AllPlayers(CirculateMessage circulateMessage){
        for (Player p : playerList){
            sendMessage(p, circulateMessage);
        }
        if (circulateMessage.getMessage().equals(SystemMessage.ACTION_ABSTAIN) || circulateMessage.getMessage().equals(SystemMessage.WORD_IS_INVALID)
                || circulateMessage.getMessage().equals(SystemMessage.WORD_REPEATED)  || circulateMessage.getMessage().equals(SystemMessage.WORD_DOES_NOT_EXIST) ){
            mediaPlayer[5].start();
        }else{
            mediaPlayer[4].start();
        }
    }
    protected void sendMessage(Player player, CirculateMessage message) {
        if (player != null && player.getGameView() != null)
            player.getGameView().addMessage(message);
    }
    protected void addChatBubble(String author, String message) {
        this.addChatBubble(author, message, -1, -1, -1, -1);
    }
    protected void addChatBubble(String author, String word, int score, int life, int round, int iconResId) {
        for (Player p : playerList) {
            if (p != null && p.getGameView() != null) {
                if (p.getName().equals(author)) {
                    p.getGameView().addChatBubble(UserType.SELF, word);
                } else if (author.equals(SYSTEM_TAG)) {
                    p.getGameView().addChatBubble(UserType.SYSTEM, word);
                } else
                    p.getGameView().addChatBubble(UserType.OTHER, author, word, score, life, round, iconResId);
            }
        }
        //play sound effect
        if (mediaPlayer[0] !=null )mediaPlayer[0].start();
    }
    private void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer){
        for (Player p : playerList){
            if (p!=null && p.getGameView()!=null)
                p.getGameView().setPlayerQueueBar(playerList, currentPlayer, leftPlayer);
        }
    }
    protected synchronized boolean isGameOver() {
        int numberOfTheAlive = 0;
        boolean isHumanAlive = false;
        for (Player player : playerList) {
            if (player.getChance() > 0){
                numberOfTheAlive++;
                if (player.getPlayerType() == Player.TYPE_HUMAN) isHumanAlive = true;
            }
        }
        if (numberOfTheAlive > 1 && isHumanAlive)
            return false;
        //play game over sound effect first
        mediaPlayer[3].start();
        return true;
    }
    public synchronized void pickNextPlayer() {
        if(currentPlayer != null && currentPlayer.getChance() > 0 ) {
            playerQueue.add(currentPlayer);
        }
        //set queue bar
        ArrayList<Player> list = new ArrayList<>();
        list.addAll(playerQueue);
        Log.i(log_name, "pickNextPlayer: playerQueue.size()="+playerQueue.size());
        setPlayerQueueBar(list, null, null);
        currentPlayer = playerQueue.remove(0);
        if(currentPlayer.getChance() <= 0)
            pickNextPlayer();
    }
    public abstract void startTurn();
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

    public abstract void checkBonus();

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
            for (int i=0; i<mediaPlayer.length; i++){
                if (mediaPlayer[i] != null) mediaPlayer[i].release();
            }
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
