package com.hifeful.notekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class NoteDatabase {
    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;

    private static final String[] ALL_COLUMNS = {
            NoteDatabaseHelper.COLUMN_ID,
            NoteDatabaseHelper.COLUMN_TITLE,
            NoteDatabaseHelper.COLUMN_TEXT,
            NoteDatabaseHelper.COLUMN_DATE,
            NoteDatabaseHelper.COLUMN_COLOR
    };

    public NoteDatabase(Context context) {
        this.helper = new NoteDatabaseHelper(context);
    }

    public void open() {
        database = helper.getWritableDatabase();
    }

    public void create(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabaseHelper.COLUMN_TITLE, note.getTitle());
        contentValues.put(NoteDatabaseHelper.COLUMN_TEXT, note.getText());
        contentValues.put(NoteDatabaseHelper.COLUMN_COLOR, note.getColor());
        contentValues.put(NoteDatabaseHelper.COLUMN_DATE, note.getDate().getTime());

        long id = database.insert(NoteDatabaseHelper.TABLE_NOTES, null, contentValues);
        note.setId(id);
    }

    public void update(Note note) {
        String whereClause = NoteDatabaseHelper.COLUMN_ID + "=" + note.getId();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabaseHelper.COLUMN_TITLE, note.getTitle());
        contentValues.put(NoteDatabaseHelper.COLUMN_TEXT, note.getText());
        contentValues.put(NoteDatabaseHelper.COLUMN_COLOR, note.getColor());
        contentValues.put(NoteDatabaseHelper.COLUMN_DATE, note.getDate().getTime());

        database.update(NoteDatabaseHelper.TABLE_NOTES, contentValues, whereClause, null);
    }

    public void delete(Long id) {
        String whereClause = NoteDatabaseHelper.COLUMN_ID + "=" + id;

        database.delete(NoteDatabaseHelper.TABLE_NOTES, whereClause, null);
    }

    public ArrayList<Note> getAll() {
        ArrayList<Note> notes = new ArrayList<>();

        Cursor cursor = null;

        long id;
        String title;
        String text;
        int color;
        long date;

        try {
            cursor = database.query(NoteDatabaseHelper.TABLE_NOTES, ALL_COLUMNS,
                    null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    id = cursor.getLong(cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_ID));
                    title = cursor.getString(cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_TITLE));
                    text = cursor.getString(cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_TEXT));
                    color = cursor.getInt(cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_COLOR));
                    date = cursor.getLong(cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_DATE));

                    Note note = new Note(id, title, text, new Date(date), color);

                    notes.add(note);
                }
            }

        }catch (Exception e) {
            Log.d(MainActivity.TAG, "Beda " + e);
        }finally {
            if (cursor != null)
                cursor.close();
        }

        return notes;
    }

    public void close() {
        if (helper != null)
            helper.close();
    }
}
