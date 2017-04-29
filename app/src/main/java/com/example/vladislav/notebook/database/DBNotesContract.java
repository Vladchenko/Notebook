package com.example.vladislav.notebook.database;

import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by vladislav on 14.03.17.
 */

public final class DBNotesContract {

    private DBNotesContract() {}

    public static class Note implements BaseColumns {
        // These are a column names, thus they have to be of a String type here.
        public static final String TABLE_NAME = "notes";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String TAG = "tag";
        public static final String CREATION_DATE = "creation_date";
        public static final String MODIFICATION_DATE = "modification_date";
    }

    public static final String SQL_CREATE_TABLE_NOTES =
            "CREATE TABLE " + Note.TABLE_NAME + " (" +
                    Note._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Note.TITLE + " TEXT UNIQUE NOT NULL," +
                    Note.TEXT + " TEXT," +
                    Note.TAG + " TEXT," +
                    Note.CREATION_DATE + " TEXT," +
                    Note.MODIFICATION_DATE + " TEXT)";

    public static final String SQL_DROP_TABLE_NOTES =
            "DROP TABLE IF EXISTS " + Note.TABLE_NAME;

}