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

    private Date mDate;
    private static DBHelper sInstance;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     *  Creating an instance in case it was not created befoиуащку
     *
     */
    public static void createInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context);
        }
    }

    /**
     *
     * Retrieving an instance for database helper object.
     *
     * @return
     */
    public static DBHelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("createInstance() should be called before");
        }
        return sInstance;
    }

    /**
     *
     * No need in implemeting yet.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     *
     * No need in implementing yet.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * This method is called only when a database doesn't exist.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDataBase(db);
    }

    private static void createDataBase(SQLiteDatabase db) {
        log.info("Attempting to create database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " .");
        db.execSQL(DBNotesContract.SQL_CREATE_TABLE_NOTES);
        log.info("Database's table " + DBNotesContract.SQL_CREATE_TABLE_NOTES + " table has been created.");
    }

    /**
     *
     * Dropping database in case of necessity (say clearing all the data in it, or need of
     * restructuring it).
     * Since one may use this method only once, you may put this method to a
     * DBHelper.createInstance();
     *
     * @param context
     */
    public static void dropDataBase(Context context) {
        log.info("Removing a database");
        context.deleteDatabase(DATABASE_NAME);
        log.info("Database has been dropped");
    }

    /**
     * Preparing a note to be put to a database.
     *
     * @param note - note entity.
     * @return
     */
    public ContentValues setNoteValues(Note note) {

        ContentValues values = new ContentValues();

        values.put(DBNotesContract.Note.TITLE, note.getTitle());
        values.put(DBNotesContract.Note.TEXT, note.getText());
        values.put(DBNotesContract.Note.TAG, note.getTag());
        values.put(DBNotesContract.Note.CREATION_DATE, mSimpleDateFormat.format(note.getCreationDate()));
        values.put(DBNotesContract.Note.MODIFICATION_DATE, mSimpleDateFormat.format(note.getModificationDate()));

        return values;
    }

    private Note parseCursor(Note note, Cursor cursor) throws ParseException {

        DateFormat format = new SimpleDateFormat(Consts.DATE_TIME_FORMAT, Locale.ENGLISH);

        note.setID(cursor.getLong(cursor.getColumnIndexOrThrow(DBNotesContract.Note._ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TITLE)));
        note.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TEXT)));
        note.setTag(cursor.getString(cursor.getColumnIndexOrThrow(DBNotesContract.Note.TAG)));

        mDate = format.parse(cursor.getString(
                cursor.getColumnIndexOrThrow(DBNotesContract.Note.CREATION_DATE)));
        note.setCreationDate(mDate);
        mDate = format.parse(cursor.getString(
                cursor.getColumnIndexOrThrow(DBNotesContract.Note.MODIFICATION_DATE)));
        note.setModificationDate(mDate);

        return note;
    }

    /**
     * Loading the notes on a specific criterion, i.e filter or all the notes. Criterion does
     * filter the notes on a DBNotesContract.Note.TITLE column.
     *
     * @param searchCriterion - value that is used in a where clause in a query like
     *                        "Where title = "Note1" ". Note1 in this case is a searchCriterion.
     * @return list of notes - List<Note>
     * @throws ParseException - thrown when parsing a cursor is wrong.
     */
    public List<Note> loadNotesFromDataBase(String searchCriterion) throws ParseException {

        LinkedList<Note> notes = new LinkedList<>();
        Note note = null;
        mDate = null;
        String whereString = null;
        Cursor cursor = null;

        // When a searchCriterion is not null and not empty, it will be like
        // "WHERE title = "Some value" ", else it is null and query is to provide a list of all
        // the notes present in data base.

        if (searchCriterion != null
                && !searchCriterion.isEmpty()) {
            whereString = DBNotesContract.Note.TITLE + " = " + "\"" + searchCriterion + "\"";
        }

        try {
            cursor = DBHelper.getInstance().getReadableDatabase().query(
                            DBNotesContract.Note.TABLE_NAME,
                            null,
                            whereString,
                            null,
                            null,
                            null,
                            null);
            while (cursor.moveToNext()) {
                note = new Note();
                note = parseCursor(note, cursor);
                notes.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            DBHelper.getInstance().close();
        }

        return notes;

    }

}
