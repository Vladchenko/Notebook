package com.example.vladislav.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.test.InstrumentationRegistry;

import com.example.vladislav.notebook.MyApplication;
import com.example.vladislav.notebook.bean.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by vladislav on 12.05.17.
 */
public class DBHelperTest {

    private Note getDummyNote() {
        Note note = new Note();
        note.setTitle("Setting up a goal to be reached");
        note.setText("Check what's interesting to me");
        note.setCreationDate(new Date());
        note.setModificationDate(new Date());
        return note;
    }

    @Test
    public void testGetInstance() throws Exception {
        assertNotEquals(DBHelper.getInstance(), null);
    }

    @Test
    public void testDropDataBase() throws Exception {
        File dbFile = null;
        Context context = InstrumentationRegistry.getTargetContext();
        DBHelper.getInstance().dropDataBase(context);
        dbFile = context.getDatabasePath(DBHelper.DATABASE_NAME);
        assertFalse(dbFile.exists());
    }

    @Test
    public void testSetNoteValues() throws Exception {
        Note note = getDummyNote();
        assertEquals(DBHelper.getInstance().setNoteValues(note).getClass(),
                ContentValues.class);
    }

    @Test
    public void testLoadNotesFromDataBase() throws Exception {
        List<Note> notesList = new ArrayList<>();
        Note note = getDummyNote();
        // Saving a note to a database.
        try {
            DBHelper.getInstance().getWritableDatabase().insert(
                    DBNotesContract.Note.TABLE_NAME,
                    null,
                    DBHelper.getInstance().setNoteValues(note));
        } finally {
            DBHelper.getInstance().close();
        }
        notesList = DBHelper.getInstance().loadNotesFromDataBase(null);
        assertEquals(notesList.size(), 1);
    }

}