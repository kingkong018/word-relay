package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.UserType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by Windows7 on 12/7/2016.
 */
public class RemoteGameWindow implements GameView, Runnable {
    private Socket socket;
    private GameActionListener gameActionListener;
    private boolean nameIsSentToServer;
    private String myName;

    /**
     * Create a new RemoteGameWindow that handle the connection from the specific socket.
     */
    public RemoteGameWindow(Socket socket) throws IOException {
        this.socket = socket;
        setupConnection();
        System.out.println("Connection established");
    }

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private void setupConnection() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        new Thread(this).start();
    }
    public void run() {
        try {
            while(true) {
                CirculateMessage message = (CirculateMessage) in.readObject();
                switch(message.getMessageType()) {
                    case SystemMessage.START_GAME:
                        break;
                    case SystemMessage.WORD_SUBMITED:
                        gameActionListener.wordSubmitted(message.getWord());
                        break;
                    case SystemMessage.MY_NAME_IS:
                        myName = (message.getAuthor());
                        (new Thread() {
                            public void run() {
                                while (nameIsSentToServer == false){
                                    if (gameActionListener != null) {
                                        gameActionListener.setPlayerName(myName);
                                        nameIsSentToServer = true;
                                    }
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                    }
                                }
                            }
                        }).start();

                        break;
                    case SystemMessage.ABSTAIN:
                        gameActionListener.abstain();
                        break;
                    case SystemMessage.SKIP:
                        gameActionListener.skip();
                        break;
                    case SystemMessage.REVERSE:
                        gameActionListener.reverse();
                        break;
                    case SystemMessage.ASSIGN:
                        gameActionListener.assign(Integer.parseInt(message.getExtra()));
                        break;
                    case SystemMessage.HELP:
                        gameActionListener.help();
                        break;
                    case SystemMessage.WINDOW_CLOSED:
                        gameActionListener.windowClosed();
                        break;
                }
            }
        } catch(IOException | ClassNotFoundException e) {
            System.out.println("Connection closed");
            gameActionListener.windowClosed();
        }
    }

    private void sendMessage(CirculateMessage message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Failed to send message");
        }
    }

    @Override
    public void setGameActionListener(GameActionListener gameActionListener) {
        this.gameActionListener = gameActionListener;
    }

    @Override
    public void setStartGame() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SET_START_GAME_DIALOG);
        sendMessage(circulateMessage);
    }

    @Override
    public void addMessage(CirculateMessage message) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.ADD_MESSAGE);
        circulateMessage.setCirculateMessage(message);
        sendMessage(circulateMessage);
    }

    @Override
    public void addChatBubble(UserType userType, String message) {
        addChatBubble(userType, null, message, -1, -1, -1, -1);
    }

    @Override
    public void addChatBubble(UserType userType, String author, String message, int score, int life, int round, int iconResId) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.ADD_CHAT_BUBBLE);
        circulateMessage.setUserType(userType);
        circulateMessage.setAuthor(author);
        circulateMessage.setScore(score);
        circulateMessage.setChance(life);
        circulateMessage.setRound(round);
        circulateMessage.setWord(message);
        circulateMessage.setIconResId(iconResId);
        sendMessage(circulateMessage);
    }

    @Override
    public void activate() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.ACTIVATE);
        sendMessage(circulateMessage);
    }

    @Override
    public void deactivate() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.DEACTIVATE);
        sendMessage(circulateMessage);
    }

    @Override
    public void showResult(List<Player> sortedPlayerList) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SHOW_RESULT);
        circulateMessage.setPlayerQueue(sortedPlayerList);
        sendMessage(circulateMessage);
    }

    @Override
    public void setPlayerInfo(String name, int score, int chance, int round) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SET_PLAYER_INFO);
        circulateMessage.setScore(score);
        circulateMessage.setChance(chance);
        circulateMessage.setRound(round);
        sendMessage(circulateMessage);
    }

    @Override
    public void setLastChar(String lastChar) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SET_LAST_CHAR);
        circulateMessage.setMessage(lastChar);
        sendMessage(circulateMessage);
    }

    @Override
    public void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer) {

    }

    @Override
    public void setSkillButton(int score, int playerNumber) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SET_SKILL_BUTTON);
        circulateMessage.setExtra(playerNumber+"");
        sendMessage(circulateMessage);
    }

    @Override
    public void setAssignPlayerWindow(List<Player> playerList) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SET_ASSIGN_WINDOW);
        circulateMessage.setPlayerQueue(playerList);
        sendMessage(circulateMessage);
    }
}
