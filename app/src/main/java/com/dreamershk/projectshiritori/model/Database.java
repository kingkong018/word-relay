package com.dreamershk.projectshiritori.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.sql.*;
/**
 * Created by Windows7 on 25/6/2016.
 */
public class Database {
    private Connection c;
    public void openDB(){
        c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:word.db");
            c.setAutoCommit(false);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Database opened successfully");
    }
    public void closeDB(){
        try {
            if (c != null) c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Database closed successfully");
    }
    public boolean isThisWordInDB(String s){
        if (c != null){
            Statement stmt = null;
            try {
                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * FROM Word WHERE Word='" + s +"';" );
                int result = rs.getFetchSize();
                rs.close();
                stmt.close();
                if (result == 0){
                    return false;
                }else{
                    return true;
                }
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
        System.out.println("Database not yet opened.");
        return false;
    }
    public ArrayList<String> giveAllValidAnswers(String firstChar){
        ArrayList<String> list = new ArrayList<String>();
        if (c != null){
            Statement stmt = null;
            try {
                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT Word FROM Word WHERE FirstCharacter='" + firstChar +"';" );
                int result = rs.getFetchSize();
                if (result == 0){
                    list.add(SystemMessage.NO_VALID_WORD_IN_DATABASE);
                    return list;
                }
                while (rs.next()){
                    list.add(rs.getString("Word"));
                }
                rs.close();
                stmt.close();
                return list;
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
        System.out.println("Database not yet opened.");
        list.add(SystemMessage.DATABASE_NOT_YET_OPENED);
        return list;
    }
    public String giveARandomWord(){
        return this.giveARandomWord(0);
    }
    public String giveARandomWord (int difficulty){
        String word ="";
        if (c != null){
            Statement stmt = null;
            try {
                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT Word FROM Word WHERE Difficulty='" + difficulty +"';" );
                int result = rs.getFetchSize();
                int i = (int)(Math.random() * result);
                int count = 0;
                while (rs.next()){
                    if (count == i){
                        word = rs.getString("Word");
                        break;
                    }
                    count++;
                }
                rs.close();
                stmt.close();
                return word;
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
        System.out.println("Database not yet opened.");
        return SystemMessage.DATABASE_NOT_YET_OPENED;
    }
}
