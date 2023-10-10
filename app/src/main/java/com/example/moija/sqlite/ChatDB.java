package com.example.moija.sqlite;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ChatDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_DB.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "userid";
    public static final String COLUMN_NAME = "username";
    public static final String COLUMN_PROFILE = "profile";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_DATE = "date";
    public String tableName;

    public ChatDB(Context context, String roomcode) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.tableName = "chat_" + roomcode;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createChatRoom(db);
    }

    public void createChatRoom(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + tableName +" ("
                + COLUMN_ID + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PROFILE + " TEXT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_DATE +" TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

}