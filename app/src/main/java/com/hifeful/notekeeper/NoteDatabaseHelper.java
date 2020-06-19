package com.hifeful.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "notekeeper.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NOTES = "NOTES";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_TEXT = "TEXT";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_COLOR = "COLOR";

    public NoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_TEXT + " TEXT, " +
                COLUMN_DATE + " INT, " +
                COLUMN_COLOR + " INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
