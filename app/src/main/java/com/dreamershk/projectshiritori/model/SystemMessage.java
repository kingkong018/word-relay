package com.dreamershk.projectshiritori.model;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class SystemMessage {
    //game action
    public static String ACTION_ABSTAIN = "此回合棄權。";
    public static String ACTION_SKIP = "決定此回合略過自己。";
    public static String ACTION_HELP = "尋求電腦幫助。";
    public static String ACTION_ASSIGN = "點名";
    public static String ACTION_REVERSE = "決定逆轉次序。";
    //game rule
    public static String WORD_DOES_NOT_EXIST = "輸入了不存在的詞語。";
    public static String WORD_REPEATED = "使用重覆的詞語。";
    public static String WORD_IS_INVALID = "沒有遵循遊戲規則。";
    //game bonus
    public static String EXTRA_CHANCE = "獲得額外的生命獎勵。";
    public static String EXTRA_SKILL = "獲得隨機功能獎勵。";
    public static String ROUND_ACHIEVED = "級別達到";
    public static String SCORE_ACHIEVED = "分數達到";
    //game run
    public static String LAST_WON_PLAYER_CANNOT_ANSWER_HER_WORD = "To notify the current player that she can give out any non-repeated word.";
    public static String LAST_WON_PLAYER_CAN_GIVE_ANY_ANSWER = "To notify the last won player that she can give out any non-repeated word.";
    public static String CURRENT_PLAYER_CAN_GIVE_ANY_WORD = "To notify other players that current player can give out any non-repeated word.";
    public static String LAST_WON_PLAYER_FAIL_SO_CURRENT_PLAYER_CAN_GIVE_ANY_WORD = "To notify the last won player that the current player can give out any non-repeated word.";
    //connection
    public static String HOST_LEFT = "The host has left the room.";
    public static String PLAYER_DISCONNECTED = "The player has lost connection.";
    public static String CANNOT_CONNECT_TO_HOST = "The player cannot connect to the hosted room.";
    public static String HOST_ROOM_INITIALIZATION_FAILURE = "The host cannot start a new room.";
    //database
    public static String DATABASE_LOADING_ERROR = "The database cannot be loaded.";
    public static String DATABASE_NOT_YET_OPENED = "The database is not opened.";
    public static String NO_VALID_WORD_IN_DATABASE = "There is no valid word in the database.";
    public static String NOT_POSSIBLE_TO_ANSWER = "it is not possible to answer in the next turn, so a new word is generated.";
    //Multiplayer remote communication
    public static final int WORD_SUBMITED = 101;
    public static final int WINDOW_CLOSED = 102;
    public static final int ACTIVATE = 201;
    public static final int DEACTIVATE = 202;
    public static final int ADD_MESSAGE = 203;
    public static final int SHOW_RESULT = 204;
    public static final int SET_LAST_CHAR = 205;
    public static final int SET_PLAYER_INFO = 206;
    public static final int GET_NUMBER_OF_PLAYERS = 207;
    public static final int MY_NAME_IS = 208;
    public static final int ADD_CHAT_ITEM = 209;
    public static final int SET_SKILL_BUTTON = 210;
    public static final int SET_START_GAME_DIALOG = 211;
    public static final int ADD_CHAT_BUBBLE = 212;
    public static final int START_GAME = 213;
    public static final int GET_PLAYER_QUEUE = 214;
    public static final int SET_ASSIGN_WINDOW = 215;
    public static final int ABSTAIN = 501;
    public static final int SKIP = 502;
    public static final int REVERSE = 503;
    public static final int HELP = 504;
    public static final int ASSIGN = 505;
    public static final int TIME_UP = 506;
    public static final int TIME_ALMOST_UP = 507;

}
