package com.example.vladislav.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vladislav.notebook.Environment;
import com.example.vladislav.notebook.bean.Note;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by vladislav on 14.03.17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notes.db";
    public static final int DATABASE_VERSION = 1;
    public static final Logger log = Logger.getLogger("DBHelper");

    private static DBHelper instance;
    private Date date;
    private SimpleDateFormat sdf = new SimpleDateFormat(Environment.DATE_TIME_FORMAT);
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void createInstance(Context context) {
        // Use in case of need to recreate a database.
//        dropDataBase(context);
        if (instance == null) {
            instance = new DBHelper(context);
        }
    }

    public static DBHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("createInstance() should be called before");
        }
        return instance;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // This method is to be called only when a database doesn't exist.
    @Override
    public void onCreate(SQLiteDatabase db) {
        log.info("Attempting to create database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " .");
        db.execSQL(DBNotesContract.SQL_CREATE_TABLE_NOTES);
        log.info("Database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " table has been created.");
    }

//    public static void createDataBase() {
//        log.info("Attempting to create database " + DATABASE_NAME + ".");
//        db.execSQL(DBNotesContract.SQL_CREATE_TABLE_NOTES);
//        log.info("Database " + DATABASE_NAME + " table has been created.");
//    }

    public static void dropDataBase(Context context) {
//            mSharedPreferences = context.getSharedPreferences(
//                    Consts.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
//            mEditor = mSharedPreferences.edit();
        log.info("Removing a database");
        context.deleteDatabase(DATABASE_NAME);
        log.info("Database has been dropped");
//            mEditor.putBoolean(Consts.DATABASE_ALREADY_POPULATED, false);
//            mEditor.commit();
    }

    public ContentValues setNoteValues(Note note) {

        ContentValues values = new ContentValues();

        values.put(DBNotesContract.Note._ID, note.getmID());
        values.put(DBNotesContract.Note.TITLE, note.getmTitle());
        values.put(DBNotesContract.Note.TEXT, note.getmText());
        values.put(DBNotesContract.Note.TAG, note.getmTag());
        values.put(DBNotesContract.Note.CREATION_DATE, sdf.format(note.getmCreationDate()));
        values.put(DBNotesContract.Note.MODIFICATION_DATE, sdf.format(note.getmModificationDate()));

        return values;
    }

    public List<Note> loadAllNotesFromDataBase() throws ParseException {

        DateFormat format = new SimpleDateFormat(Environment.DATE_TIME_FORMAT, Locale.ENGLISH);
        LinkedList<Note> notes = new LinkedList<>();
        Note note = null;
        date = null;

        Cursor cursor =
                DBHelper.getInstance().getReadableDatabase().query(
                        DBNotesContract.Note.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        while (cursor.moveToNext()) {

            note = new Note();

            note.setmID(Long.parseLong(cursor.getString(0)));
            note.setmTitle(cursor.getString(1));
            note.setmText(cursor.getString(2));
            note.setmTag(cursor.getString(3));

            date = format.parse(cursor.getString(4));
            note.setmCreationDate(date);
            date = format.parse(cursor.getString(5));
            note.setmModificationDate(date);

            notes.add(note);

        }

        return notes;

    }

}
