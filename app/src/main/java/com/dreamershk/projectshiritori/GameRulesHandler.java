package com.dreamershk.projectshiritori;

import java.util.ArrayList;

/**
 * Created by Kong on 4/6/2016.
 */
public class GameRulesHandler {
    public static boolean isExist(ArrayList<String> list, String input){
        if(!list.isEmpty()){
            for (String word: list){
                if (word.equals(input)) return true;
            }
        }
        return false;
    }

    public static boolean isValid(ArrayList<String> list, String input){
        if(input.equals("") || input.length()<2) {
            return false;
        }else if(!list.isEmpty()){
            String prevWord = list.get(list.size()-1);
            Character last_character_of_preWord = prevWord.charAt(prevWord.length() - 1) ;
            Character first_character_of_input = input.charAt(0);
            if(last_character_of_preWord.equals(first_character_of_input)){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    public static boolean isAWord(ArrayList<String> word_database, String input){
        if(!word_database.isEmpty()){
            for (String word: word_database){
                if (input.equals(word)) return true;
            }
        }
        return false;
    }
}
