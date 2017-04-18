package com.example.vladislav.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vladislav.notebook.Consts;
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
    public static final int NEW_NOTE_ADDED = 1;
    public static final int NEW_NOTE_NOT_ADDED = 0;

    private Date date;
    private static DBHelper instance;
    private SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void createInstance(Context context) {
        if (instance == null) {
            dropDataBase(context);
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

    // This method is called only when a database doesn't exist.
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDataBase(db);
    }

    private static void createDataBase(SQLiteDatabase db) {
        log.info("Attempting to create database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " .");
        db.execSQL(DBNotesContract.SQL_CREATE_TABLE_NOTES);
        log.info("Database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " table has been created.");
    }

    public static void dropDataBase(Context context) {
        log.info("Removing a database");
        context.deleteDatabase(DATABASE_NAME);
        log.info("Database has been dropped");
    }

    public ContentValues setNoteValues(Note note) {

        ContentValues values = new ContentValues();

        values.put(DBNotesContract.Note.TITLE, note.getTitle());
        values.put(DBNotesContract.Note.TEXT, note.getText());
        values.put(DBNotesContract.Note.TAG, note.getTag());
        values.put(DBNotesContract.Note.CREATION_DATE, sdf.format(note.getCreationDate()));
        values.put(DBNotesContract.Note.MODIFICATION_DATE, sdf.format(note.getModificationDate()));

        return values;
    }

    private void parseCursor(Note note, Cursor cursor) throws ParseException {

        DateFormat format = new SimpleDateFormat(Consts.DATE_TIME_FORMAT, Locale.ENGLISH);

        note.setID(cursor.getLong(cursor.getColumnIndexOrThrow(DBNotesContract.Note._ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TITLE)));
        note.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TEXT)));
        note.setTag(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TAG)));

        date = format.parse(cursor.getString(
                cursor.getColumnIndexOrThrow(DBNotesContract.Note.CREATION_DATE)));
        note.setCreationDate(date);
        date = format.parse(cursor.getString(
                cursor.getColumnIndexOrThrow(DBNotesContract.Note.MODIFICATION_DATE)));
        note.setModificationDate(date);

    }

    public List<Note> loadNotesFromDataBase(String searchCriterion) throws ParseException {

        LinkedList<Note> notes = new LinkedList<>();
        Note note = null;
        date = null;
        String whereString = null;

        if (searchCriterion != null
                && !searchCriterion.isEmpty()) {
            whereString = DBNotesContract.Note.TITLE + " = " + searchCriterion;
        }

        Cursor cursor =
                DBHelper.getInstance().getReadableDatabase().query(
                        DBNotesContract.Note.TABLE_NAME,
                        null,
                        whereString,
                        null,
                        null,
                        null,
                        null);

        while (cursor.moveToNext()) {
            note = new Note();
            parseCursor(note, cursor);
            notes.add(note);
        }

        cursor.close();

        return notes;

    }

}
