package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.Database;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.WordDictionary;
import com.dreamershk.projectshiritori.model.WordItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kong on 4/6/2016.
 */
public class GameRulesHandler {

    public static ArrayList<WordDictionary> database;
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
    public static boolean isAWord(String input){
        String firstChar = input.substring(0,1);
        List<String> list = giveAllValidAnswers(firstChar);
        for (int j=0; j<list.size(); j++){
            if (list.get(j).equals(input)){
                return true;
            }
        }
        return false;
    }
    public static boolean isAWord(ArrayList<String> word_database, String input){
        if(!word_database.isEmpty()){
            for (String word: word_database){
                if (input.equals(word)) return true;
            }
        }
        return false;
    }
    public static List<String> giveAllValidAnswers(String firstChar){
        List<String> result = new ArrayList<String>();
        boolean isAnswerFound = false;
        for (int i=0; i<database.size(); i++){
                WordDictionary dict = database.get(i);
                if (dict.firstChar.equals(firstChar)){
                    result = dict.list;
                    isAnswerFound = true;
                }
        }
        if (!isAnswerFound) result.add(SystemMessage.NO_VALID_WORD_IN_DATABASE);
        return result;
    }
    public static String giveARandomWord(){
        int i = (int)(Math.random() * database.size());
        WordDictionary dict = database.get(i);
        i = (int)(Math.random() * dict.list.size());
        return dict.list.get(i);
    }
    public static String giveARandomWord(int difficulty){
        return giveARandomWord();
    }
}
