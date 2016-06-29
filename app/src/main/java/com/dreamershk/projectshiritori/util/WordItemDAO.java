package com.dreamershk.projectshiritori.util;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.WordDictionary;
import com.dreamershk.projectshiritori.model.WordItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by Windows7 on 27/6/2016.
 */
public class WordItemDAO {
    public final static String TABLE_NAME_4_First_Char = "FirstCharacter";
    public final static String TABLE_NAME_4_Word = "Word";

    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String FIRST_CHAR_COLUMN = "firstchar";
    public static final String LAST_CHAR_COLUMN = "lastchar";
    public static final String WORDCOUNT_COLUMN = "wordcount";
    public static final String DIFFICULTY_COLUMN = "difficulty";
    public static final String WORD_COLUMN = "word";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE_4_First_Char =
            "CREATE TABLE " + TABLE_NAME_4_First_Char + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIRST_CHAR_COLUMN + " TEXT NOT NULL, " +
                    WORDCOUNT_COLUMN + " INTEGER NOT NULL, " +
                    DIFFICULTY_COLUMN + " INTEGER NOT NULL)";
    public static final String CREATE_TABLE_4_Word =
            "CREATE TABLE " + TABLE_NAME_4_Word + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WORD_COLUMN + " TEXT NOT NULL, " +
                    FIRST_CHAR_COLUMN + " TEXT NOT NULL, " +
                    LAST_CHAR_COLUMN + " TEXT NOT NULL, " +
                    DIFFICULTY_COLUMN + " INTEGER NOT NULL)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public WordItemDAO(Context context) {
        db = WordDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public WordItem insert2FirstChar(WordItem item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(FIRST_CHAR_COLUMN, item.getFirstChar());
        cv.put(WORDCOUNT_COLUMN, item.getWordCount());
        cv.put(DIFFICULTY_COLUMN, item.getDifficulty());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME_4_First_Char, null, cv);

        // 設定編號
        item.setID(id);
        // 回傳結果
        return item;
    }
    public WordItem insert2Word(WordItem item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(WORD_COLUMN, item.getWord());
        cv.put(FIRST_CHAR_COLUMN, item.getFirstChar());
        cv.put(LAST_CHAR_COLUMN, item.getLastChar());
        cv.put(DIFFICULTY_COLUMN, item.getDifficulty());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME_4_Word, null, cv);

        // 設定編號
        item.setID(id);
        // 回傳結果
        return item;
    }
    public boolean update2FirstChar(WordItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(FIRST_CHAR_COLUMN, item.getFirstChar());
        cv.put(WORDCOUNT_COLUMN, item.getWordCount());
        cv.put(DIFFICULTY_COLUMN, item.getDifficulty());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getID();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME_4_First_Char, cv, where, null) > 0;
    }
    public boolean update2Word(WordItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(WORD_COLUMN, item.getWord());
        cv.put(FIRST_CHAR_COLUMN, item.getFirstChar());
        cv.put(LAST_CHAR_COLUMN, item.getLastChar());
        cv.put(DIFFICULTY_COLUMN, item.getDifficulty());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getID();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME_4_Word, cv, where, null) > 0;
    }
    public boolean deleteFromFirstChar(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME_4_First_Char, where , null) > 0;
    }
    public boolean deleteFromWord(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME_4_Word, where , null) > 0;
    }
    public List<WordItem> getAllFromFirstChar() {
        List<WordItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME_4_First_Char, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecordFromFirstChar(cursor));
        }
        cursor.close();
        return result;
    }
    public List<WordItem> getAllFromWord() {
        List<WordItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME_4_Word, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecordFromWord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<WordItem> getAllFromWord(String firstChar) {
        List<WordItem> result = new ArrayList<>();
        String where = FIRST_CHAR_COLUMN + "='" + firstChar +"'";
        Cursor cursor = db.query(
                TABLE_NAME_4_Word, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecordFromWord(cursor));
        }
        cursor.close();
        return result;
    }
    public WordItem getFromFirstChar(long id) {
        // 準備回傳結果用的物件
        WordItem item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME_4_First_Char, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecordFromFirstChar(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }
    public WordItem getFromWord(long id) {
        // 準備回傳結果用的物件
        WordItem item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME_4_Word, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecordFromWord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }
    public WordItem getFromWord(String word) {
        // 準備回傳結果用的物件
        WordItem item = null;
        // 使用編號為查詢條件
        String where = WORD_COLUMN + "=" + word;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME_4_Word, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecordFromWord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }
    public WordItem getFromWord(Character firstChar) {
        // 準備回傳結果用的物件
        WordItem item = null;
        // 使用編號為查詢條件
        String where = FIRST_CHAR_COLUMN + "=" + firstChar.toString();
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME_4_Word, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecordFromWord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }
    public WordItem getRecordFromFirstChar(Cursor cursor) {
        // 準備回傳結果用的物件
        long id = cursor.getLong(0);
        String firstChar = cursor.getString(1);
        int wordCount = cursor.getInt(2);
        int difficulty = cursor.getInt(3);
        WordItem result = new WordItem(id,firstChar,wordCount,difficulty);

        // 回傳結果
        return result;
    }
    public WordItem getRecordFromWord(Cursor cursor) {
        // 準備回傳結果用的物件
        long id = cursor.getLong(0);
        String word = cursor.getString(1);
        String firstChar = cursor.getString(2);
        String lastChar = cursor.getString(3);
        int difficulty = cursor.getInt(4);
        WordItem result = new WordItem(id,word,firstChar,lastChar,difficulty);

        // 回傳結果
        return result;
    }
    public int getCount(String tableName) {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
    public void deleteAllEntries(){
        db.execSQL("DROP TABLE IF EXISTS " + WordItemDAO.TABLE_NAME_4_First_Char);
        db.execSQL("DROP TABLE IF EXISTS " + WordItemDAO.TABLE_NAME_4_Word);
        // 呼叫onCreate建立新版的表格
        db.execSQL(WordItemDAO.CREATE_TABLE_4_First_Char);
        db.execSQL(WordItemDAO.CREATE_TABLE_4_Word);
    }
    public ArrayList<WordDictionary> loadDataFromRawFile(ArrayList<WordDictionary> wordList){
        try{
            //Load into database.
            for (int i=0; i< wordList.size(); i++){
                WordDictionary dict = wordList.get(i);
                //FirstCharacter Table
                String firstChar = dict.firstChar;
                int wordCount = dict.list.size();
                // set Diffculty
                // 0 Easy: >=10words;
                // 1 Medium: 6-10 words;
                // 2 Difficult: 2-5 words;
                // 3 Impossible: 1 word.
                int difficulty = 0;
                if (wordCount == 1){
                    difficulty = 3;
                }else if (wordCount <= 5){
                    difficulty = 2;
                }else if (wordCount < 10){
                    difficulty = 1;
                }
                WordItem item = new WordItem(firstChar, wordCount, difficulty);
                insert2FirstChar(item);

                //Word Table
                for (int j=0; j<dict.list.size(); j++){
                    String word = dict.list.get(j);
                    String lastChar = word.substring(word.length()-1);
                    difficulty = 0; //update later...until firstChar Table complete...
                    item = new WordItem(word,firstChar,lastChar,difficulty);
                    insert2Word(item);
                    System.out.println("Added: " + firstChar);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return wordList;
    }
    public void processData(WordDictionary dict){
        try{
            db.beginTransaction();
            //FirstCharacter Table
            String firstChar = dict.firstChar;
            int wordCount = dict.list.size();
            // set Diffculty
            // 0 Easy: >=10words;
            // 1 Medium: 6-10 words;
            // 2 Difficult: 2-5 words;
            // 3 Impossible: 1 word.
            int difficulty = 0;
            if (wordCount == 1){
                difficulty = 3;
            }else if (wordCount <= 5){
                difficulty = 2;
            }else if (wordCount < 10){
                difficulty = 1;
            }
            WordItem item = new WordItem(firstChar, wordCount, difficulty);
            insert2FirstChar(item);
            //Word Table
            for (int j=0; j<dict.list.size(); j++){
                String word = dict.list.get(j);
                String lastChar = word.substring(word.length()-1);
                difficulty = 0; //update later...until firstChar Table complete...
                item = new WordItem(word,firstChar,lastChar,difficulty);
                insert2Word(item);
            }
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
        }
    }
}
