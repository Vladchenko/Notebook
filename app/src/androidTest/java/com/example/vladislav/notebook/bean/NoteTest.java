package com.example.vladislav.notebook.bean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by vladislav on 12.05.17.
 */
public class NoteTest {

    Note note1;
    Note note2;

    private Note getDummyNote() {
        Note note = new Note();
        note.setTitle("Setting up a goal to be reached");
        note.setText("Check what's interesting to me");
        note.setCreationDate(new Date());
        note.setModificationDate(new Date());
        return note;
    }

    @Before
    public void setUp() throws Exception {
        note1 = getDummyNote();
        note2 = getDummyNote();
    }

    @After
    public void tearDown() throws Exception {
        note1 = null;
        note2 = null;
    }

    @Test
    public void testEqualsWithSameValues() throws Exception {
        assertTrue(note1.equals(note2));
    }

    @Test
    public void testEqualsWithDifferentValues() throws Exception {
        note2.setTitle("Another title");
        assertFalse(note1.equals(note2));
    }

    @Test
    public void testHashCodeWithSameValues() throws Exception {
        assertTrue(note1.hashCode() == note2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentValues() throws Exception {
        note2.setTitle("Another title");
        assertFalse(note1.hashCode() == note2.hashCode());
    }

    @Test
    public void testToStringNoteNotPopulated() throws Exception {
        note1 = new Note();
        assertEquals(note1.toString(), "Note { Title='null', Text='null', Tag='null' }");
    }

    @Test
    public void testToStringDummyNote() throws Exception {
        assertEquals(note1.toString(), "Note { Title='" + note1.getTitle()
                + "', Text='" + note1.getText()
                + "', Tag='" + note1.getTag()
                + "' }");
    }

}