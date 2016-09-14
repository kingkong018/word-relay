package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by Windows7 on 12/7/2016.
 */
public class RemoteGameActionHandler implements GameActionListener, Runnable {
    private MultiPlayerGameWindow gameWindow;
    private Socket socket;
    private String myName;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public RemoteGameActionHandler(String host, int port, MultiPlayerGameWindow g, String name) throws IOException {
        this.gameWindow = g;
        this.myName = name;
        final String h =host;
        final int p = port;
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    socket = new Socket(h,p);
                    setupConnection();
                    System.out.println("Connection established");
                    gameWindow.setConnected();
                    setPlayerName(myName);
                } catch (IOException e) {
                    e.printStackTrace();
                    CirculateMessage circulateMessage = new CirculateMessage();
                    circulateMessage.setMessage(SystemMessage.CANNOT_CONNECT_TO_HOST);
                    gameWindow.addMessage(circulateMessage);
                    gameWindow.finish();
                }
            }
        });
        thread.start();
    }

    private void setupConnection() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        new Thread(this).start();
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
    public void startGame() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.START_GAME);
        sendMessage(circulateMessage);
    }

    @Override
    public void wordSubmitted(String input) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.WORD_SUBMITED);
        circulateMessage.setWord(input);
        sendMessage(circulateMessage);
    }

    @Override
    public void abstain() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.ABSTAIN);
        sendMessage(circulateMessage);
    }

    @Override
    public void skip() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.SKIP);
        sendMessage(circulateMessage);
    }

    @Override
    public void help() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.HELP);
        sendMessage(circulateMessage);
    }

    @Override
    public void assign(int playerHashCode) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.ASSIGN);
        circulateMessage.setExtra(playerHashCode+"");
        sendMessage(circulateMessage);
    }

    @Override
    public void reverse() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.REVERSE);
        sendMessage(circulateMessage);
    }

    @Override
    public void windowClosed() {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.WINDOW_CLOSED);
        sendMessage(circulateMessage);
    }

    @Override
    public void setPlayerName(String name) {
        CirculateMessage circulateMessage = new CirculateMessage();
        circulateMessage.setMessageType(SystemMessage.MY_NAME_IS);
        circulateMessage.setAuthor(name);
        sendMessage(circulateMessage);
    }

    @Override
    public int getNumberOfPlayers() {
        //
        return -1;
    }

    @Override
    public List<Player> getPlayerQueue() {
        //
        return null;
    }

    @Override
    public void timeUp() {
        //
    }

    @Override
    public void timeAlmostUp() {
        //
    }

    @Override
    public List<String> getWordList() {
        return null;
    }

    @Override
    public void run() {
        try {
            while(true) {
                CirculateMessage message = (CirculateMessage)in.readObject();
                switch(message.getMessageType()) {
                    case SystemMessage.ACTIVATE:
                        gameWindow.activate();
                        break;
                    case SystemMessage.DEACTIVATE:
                        gameWindow.deactivate();
                        break;
                    case SystemMessage.ADD_MESSAGE:
                        gameWindow.addMessage(message.getCirculateMessage());
                        break;
                    case SystemMessage.SHOW_RESULT:
                        gameWindow.showResult(message.getPlayerQueue());
                        break;
                    case SystemMessage.SET_PLAYER_INFO:
                        gameWindow.setPlayerInfo(message.getAuthor(), message.getScore(), message.getChance(), message.getRound());
                        break;
                    case SystemMessage.ADD_CHAT_BUBBLE:
                        gameWindow.addChatBubble(message.getUserType(), message.getAuthor(), message.getWord(), message.getScore(), message.getChance(), message.getRound(), message.getIconResId());
                        break;
                    case SystemMessage.SET_START_GAME_DIALOG:
                        break;
                    case SystemMessage.SET_LAST_CHAR:
                        gameWindow.setLastChar(message.getMessage());
                        break;
                    case SystemMessage.SET_SKILL_BUTTON:
                        gameWindow.setSkillButton(message.getScore(), Integer.parseInt(message.getExtra()));
                        break;
                    case SystemMessage.SET_ASSIGN_WINDOW:
                        gameWindow.setAssignPlayerWindow(message.getPlayerQueue());
                        break;
                }
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Connection closed");
        }
    }
}
