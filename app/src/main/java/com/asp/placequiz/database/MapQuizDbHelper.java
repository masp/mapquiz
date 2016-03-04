package com.asp.placequiz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MapQuizDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "mapquiz.db";

    private static MapQuizDbHelper instance;

    public static synchronized MapQuizDbHelper getHelper(Context context) {
        if (instance == null)
            instance = new MapQuizDbHelper(context);
        return instance;
    }

    private MapQuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        execList(db, MapQuizContract.SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        execList(db, MapQuizContract.SQL_DELETE_TABLES);
        onCreate(db);
    }

    private void execList(SQLiteDatabase db, String[] statements) {
        for (String statement : statements) {
            db.execSQL(statement);
        }
    }
}