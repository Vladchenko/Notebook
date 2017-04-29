package com.example.vladislav.notebook;

import android.content.Intent;
import android.database.Cursor;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;
import com.example.vladislav.notebook.noteslist.NotesListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.contrib.RecyclerViewActions;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.not;

/**
 * Created by vladislav on 25.04.17.
 */

public class EspressoTest {

    final String NOTE_TITLE_TEST_TEXT = "test text";
    final String NOTE_EDITING_TEXT = " edited";
    final String NOTE_TEXT_TEST_TEXT = "test text";
    String loadedTitle;
    String typedNoteTitle;
    String typedNoteText;
    Intent i = new Intent();

    @Rule
    public ActivityTestRule<NotesListActivity> mNotesListActivityRule = new ActivityTestRule<>(
            NotesListActivity.class);
    @Rule
    public ActivityTestRule<NoteActivity> mNoteActivityRule = new ActivityTestRule<>(
            NoteActivity.class);

    @Before
    public void setUp() throws Exception {
        i = new Intent();
        mNotesListActivityRule.launchActivity(i);
    }

    @After
    public void tearDown() throws Exception {
        i = null;
        mNotesListActivityRule.getActivity().finish();
    }

    private void addButtonClick() {

        onView(withId(R.id.add_button)).perform(click());

        // Following happens in another activity.
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_save_button)).check(matches(isDisplayed()));

        onView(withId(R.id.note_content_title_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.note_content_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_text_view)).check(matches(isDisplayed()));

    }

    private void deleteButtonClick() {

        onView(withId(R.id.delete_button)).perform(click());

        // This happens in the same activity.
        onView(withId(R.id.search_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.add_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.commit_operation_button)).check(matches(isDisplayed()));
        // Delete button serves as cancel button in this case.
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

    }

    private void searchButtonClick() {

        onView(withId(R.id.search_button)).perform(click());

        // Following happens in the same activity.
        onView(withId(R.id.search_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.commit_operation_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_edit_text)).check(matches(isDisplayed()));

    }

    private void createNewNote() {

        // What's done next, to add a new note.
        onView(withId(R.id.note_title_edit_text)).perform(typeText(NOTE_TITLE_TEST_TEXT));
        closeSoftKeyboard();
        onView(withId(R.id.note_content_edit_text)).perform(typeText(NOTE_TEXT_TEST_TEXT));
        closeSoftKeyboard();

        Date dateTime = new Date();
        typedNoteTitle = ((TextView) mNoteActivityRule.getActivity().
                findViewById(R.id.note_title_edit_text)).getText().toString();
        typedNoteText = ((TextView) mNoteActivityRule.getActivity().
                findViewById(R.id.note_content_title_text_view)).getText().toString();

        // Populating a note bean for further saving it to a data base.
        Note note = new Note(
                typedNoteTitle,
                typedNoteText,
                "",
                dateTime,
                dateTime);

        // Triggering an uploading of a note to a database.
        onView(withId(R.id.save_button)).perform(click());

    }

    @Test
    public void testAddButtonClick() {
        addButtonClick();
    }

    @Test
    public void testDeleteButtonClick() throws Exception {
        deleteButtonClick();
    }

    @Test
    public void testSearchButtonClick() throws Exception {
        searchButtonClick();
    }

    @Test
    public void testNewNoteCreatingAndDeleting() throws Exception {

        Cursor cursor = null;

        testAddButtonClick();

        createNewNote();

        try {
            cursor =
                    DBHelper.getInstance().getReadableDatabase().query(
                            DBNotesContract.Note.TABLE_NAME,
                            null,
                            DBNotesContract.Note.TITLE + " = \"" + NOTE_TITLE_TEST_TEXT + "\"",
                            null,
                            null,
                            null,
                            null);
            cursor.moveToFirst();
            loadedTitle = cursor.getString(cursor.getColumnIndex(DBNotesContract.Note.TITLE));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            DBHelper.getInstance().close();
        }

        try {
            DBHelper.getInstance().getReadableDatabase().delete(
                    DBNotesContract.Note.TABLE_NAME,
                    DBNotesContract.Note.TITLE + " = \""
                            + loadedTitle + "\"",
                    null
            );
        } finally {
            DBHelper.getInstance().close();
        }

        assertEquals(loadedTitle, NOTE_TITLE_TEST_TEXT);

    }

    @Test
    public void testDeleteNoteByCheckMark() throws Exception {

        addButtonClick();
        createNewNote();
        deleteButtonClick();

        // Getting an item in a recyclerview and clicking its deletion checkmark.
        onView(withId(R.id.recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, MyViewAction.clickChildViewWithId(R.id.deletion_check_box)));

        onView(withId(R.id.commit_operation_button)).perform(click());

    }

    @Test
    public void testSearchNote() throws Exception {

        addButtonClick();
        createNewNote();
        searchButtonClick();

        // What's done next, to find any note.
        onView(withId(R.id.search_edit_text)).perform(typeText(NOTE_TITLE_TEST_TEXT));
        closeSoftKeyboard();

        // Triggering a search.
        onView(withId(R.id.commit_operation_button)).perform(click());

        RecyclerView recyclerView = (RecyclerView)mNotesListActivityRule.
                getActivity().findViewById(R.id.recycler);

        // Since there has to be only one item present in recyclerview (we created one note),
        // this assertion must be true.
        assertEquals(recyclerView.getAdapter().getItemCount(), 1);
    }

//    @Test
//    public void testEditNote() throws Exception {
//
//        addButtonClick();
//        createNewNote();
//
//        // Getting an item in a recyclerview and clicking it.
//        onView(withId(R.id.recycler)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(
//                        0, click()));
//
//        // Editing the note.
//        onView(withId(R.id.note_title_edit_text)).perform(typeText(NOTE_EDITING_TEXT));
//        closeSoftKeyboard();
//        onView(withId(R.id.note_content_edit_text)).perform(typeText(NOTE_EDITING_TEXT));
//        closeSoftKeyboard();
//
//        // Triggering saving a note to a database.
//        onView(withId(R.id.save_button)).perform(click());
//
//        // Getting an item in a recyclerview and clicking it again.
//        onView(withId(R.id.recycler)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(
//                        0, click()));
//
//        // WHY IS THIS VALUE EMPTY ?
//        System.out.println("|" + ((EditText)mNoteActivityRule.
//                getActivity().findViewById(R.id.note_title_edit_text)).getText().toString() + "|");
//
//        // Comparing the text that has to be present in a title with an actual
//        // recyclerview item title text.
//        assertEquals(NOTE_TITLE_TEST_TEXT + NOTE_EDITING_TEXT, "");
//    }

    @Test
    public void testPopupEditNote() throws Exception {

        addButtonClick();
        createNewNote();

        // Getting an item in a recyclerview and clicking it again.
        onView(withId(R.id.recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, longClick()));

        // Clicking an edit menu item.
        onView(withText("Edit")).perform(click());

        onView(withId(R.id.note_activity)).check(matches(isDisplayed()));

    }

    /**
     * The goal is to test if the number of a notes before deletion and a number of them after
     * deletion differ in only one.
     * @throws Exception
     */
    @Test
    public void testPopupDeleteNote() throws Exception {

        int recyclerViewItemsNumber = 0;

        addButtonClick();
        createNewNote();

        RecyclerView recyclerView = ((RecyclerView) mNotesListActivityRule.
                getActivity().findViewById(R.id.recycler));

        // Getting a number of an items in an adapter of a recyclerview
        recyclerViewItemsNumber = ((RecyclerView) mNotesListActivityRule.
                getActivity().findViewById(R.id.recycler)).getAdapter().getItemCount();

        // Getting an item in a recyclerview and clicking it again.
        onView(withId(R.id.recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, longClick()));

        // Clicking an edit menu item.
        onView(withText("Delete")).perform(click());

        assertEquals(recyclerViewItemsNumber - 1, recyclerView.getAdapter().getItemCount());
    }

}
