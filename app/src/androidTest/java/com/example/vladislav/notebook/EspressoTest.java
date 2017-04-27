package com.example.vladislav.notebook;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;
import com.example.vladislav.notebook.noteslist.NotesListActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

/**
 * Created by vladislav on 25.04.17.
 */

public class EspressoTest {


    @Rule
    public ActivityTestRule<NotesListActivity> mNotesListActivityRule = new ActivityTestRule<>(
            NotesListActivity.class);
    @Rule
    public ActivityTestRule<NoteActivity> mNoteActivityRule = new ActivityTestRule<>(
            NoteActivity.class);

    @Before
    public void setUp() throws Exception {
        Intent i = new Intent();
        mNotesListActivityRule.launchActivity(i);
    }

    @Test
    public void onAddButtonClick() {

        onView(withId(R.id.add_button)).perform(click());

        // Following happens in another activity.
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_save_button)).check(matches(isDisplayed()));

        onView(withId(R.id.note_content_title_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.note_content_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_text_view)).check(matches(isDisplayed()));

    }

    @Test
    public void onDeleteButtonClick() throws Exception {

        onView(withId(R.id.delete_button)).perform(click());

        // This happens in the same activity.
        onView(withId(R.id.search_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.add_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.commit_operation_button)).check(matches(isDisplayed()));
        // Delete button serves as cancel button in this case.
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

    }

    @Test
    public void onSearchButtonClick() throws Exception {

        onView(withId(R.id.search_button)).perform(click());

        // This happens in the same activity.
        onView(withId(R.id.search_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.commit_operation_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_edit_text)).check(matches(isDisplayed()));

    }

    @Test
    public void onNewNoteCreating() throws Exception {

        onAddButtonClick();

        // What's done next to add a new note;
        onView(withId(R.id.note_title_edit_text)).perform(typeText("test text"));
        closeSoftKeyboard();
        onView(withId(R.id.note_content_edit_text)).perform(typeText("test text"));
        closeSoftKeyboard();

        Date dateTime = new Date();

        // Populating a note bean for further saving it to a data base.
        Note note = new Note(
                ((TextView)mNoteActivityRule.getActivity().
                        findViewById(R.id.note_title_edit_text)).getText().toString(),
                ((TextView)mNoteActivityRule.getActivity().
                        findViewById(R.id.note_content_title_text_view)).getText().toString(),
                "",
                dateTime,
                dateTime);

        // Saving a mNote to a database.
        DBHelper.getInstance().getWritableDatabase().insert(
                DBNotesContract.Note.TABLE_NAME,
                null,
                DBHelper.getInstance().setNoteValues(note));
    }

}
