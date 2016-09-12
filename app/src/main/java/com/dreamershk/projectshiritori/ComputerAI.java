package com.dreamershk.projectshiritori;

import com.dreamershk.projectshiritori.model.SystemMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows7 on 18/7/2016.
 */
public class ComputerAI {
    public static String playByComputer(ArrayList<String> history, String lastValidWord, int score) {
        int i;
        //ramdomly search for a suitable answer
        String word = "";
        if (history.size() == 0) {
            word = GameRulesHandler.giveARandomWord();
        }else {
            List<String> list = GameRulesHandler.giveAllValidAnswers(lastValidWord.substring(lastValidWord.length() - 1));
            i = (int)(Math.random() * 100);
            if (list.size() < 10 && i < 50){ //No Answer
                if (score > GameManager.REVERSE_POINT){
                    i = (int)(Math.random() * 3);
                    switch (i){
                        case 0:
                            word = SystemMessage.ACTION_HELP;
                            break;
                        case 1:
                            word = SystemMessage.ACTION_REVERSE;
                            break;
                        case 2:
                            word = SystemMessage.ACTION_SKIP;
                            break;
                    }
                }else if(score > GameManager.HELP_POINT){
                    i = (int)(Math.random() * 2);
                    switch (i){
                        case 0:
                            word = SystemMessage.ACTION_HELP;
                            break;
                        case 1:
                            word = SystemMessage.ACTION_SKIP;
                            break;
                    }
                }else{
                    word = GameRulesHandler.giveARandomWord();
                }
            }else{
                do {
                    //Use lastValidWord instead of answer because answer may be incorrect.
                    i = (int)(Math.random() * list.size());
                    word = list.get(i);
                }while (GameRulesHandler.isExist(history, word));
            }
        }
        return word;
    }
}
