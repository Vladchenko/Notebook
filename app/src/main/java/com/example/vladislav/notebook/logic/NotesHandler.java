package com.example.vladislav.notebook.logic;

import com.example.vladislav.notebook.bean.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by vladislav on 13.04.17.
 */

public class NotesHandler {

    private LinkedList<Note> notes = new LinkedList<>();

    public void addNote(Note note) {
        notes.add(note);
    }

//    public void addNote(
//            String title,
//            String text,
//            String tag,
//            Date creationDate,
//            Date ModifiedDate) {
//        notes.add(note);
//    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public LinkedList<Note> getNotes() {
        return notes;
    }

    public void setNotes(LinkedList<Note> notes) {
        this.notes = notes;
    }

}
