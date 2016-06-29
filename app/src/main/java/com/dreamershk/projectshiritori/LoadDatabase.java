package com.dreamershk.projectshiritori;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Kong on 4/6/2016.
 */
public class LoadDatabase {
    private static ArrayList<String> word_database = new ArrayList<String>();
    public static ArrayList<String> getDatabase(Context context){
        if (word_database.isEmpty()) {
            try{
                AssetManager am = context.getAssets();
                InputStream is = null;
                BufferedReader br = null;
                is = am.open("worddatabase.txt");
                br = new BufferedReader((new InputStreamReader(is, "UTF-8")));
                String line = null;
                while((line = br.readLine()) != null){
                    String[] listOfWord = line.split(" ");
                    for (String eachWordInArray : listOfWord){
                        word_database.add(eachWordInArray);
                    }
                }
                br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return word_database;
    }
}
