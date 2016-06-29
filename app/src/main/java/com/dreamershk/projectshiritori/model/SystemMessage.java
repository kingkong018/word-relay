package com.dreamershk.projectshiritori.model;

/**
 * Created by Windows7 on 26/6/2016.
 */
public class SystemMessage {
    //game action
    public static String DRAWN = "棄權";
    //game rule
    public static String WORD_DOES_NOT_EXIST = "輸入了不存在的詞語";
    public static String WORD_REPEATED = "使用重覆的詞語";
    public static String WORD_IS_INVALID = "沒有遵循遊戲規則";
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

}
