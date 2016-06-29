package com.dreamershk.projectshiritori.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dreamershk.projectshiritori.model.WordItem;

/**
 * Created by Windows7 on 27/6/2016.
 */
public class WordDBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "word.db";
    public static final int VERSION = 8; //if change, must add 1.
    private static SQLiteDatabase database;

    public WordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new WordDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WordItemDAO.CREATE_TABLE_4_First_Char);
        db.execSQL(WordItemDAO.CREATE_TABLE_4_Word);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + WordItemDAO.TABLE_NAME_4_First_Char);
        db.execSQL("DROP TABLE IF EXISTS " + WordItemDAO.TABLE_NAME_4_Word);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
