package com.example.moija.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RoomListDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "roomList_DB.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_NAME = "roomname";
    public static final String COLUMN_CODE = "roomcode";
    public static final String COLUMN_DATE = "roomdate";
    public static final String TABLE_NAME = "roomList_DB";
    public static final String COLUMN_NUMBER = "roomlistnumber";

    public RoomListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE roomList_DB ("
//                + COLUMN_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CODE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DATE +" TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS roomList_DB");
        onCreate(db);
    }

}