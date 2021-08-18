package com.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by G V RAVI KUMAR on 3/8/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.FeedEntry.TABLE_NAME + "(" +
                    DBContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    DBContract.FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    DBContract.FeedEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    DBContract.FeedEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    DBContract.FeedEntry.COLUMN_NAME_STATUS + " BOOLEAN"+");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.FeedEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
