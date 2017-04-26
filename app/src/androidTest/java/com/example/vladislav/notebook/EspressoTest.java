package com.example.vladislav.notebook;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.example.vladislav.notebook.noteslist.NotesListActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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

    @Before
    public void setUp() throws Exception {
        Intent i = new Intent();
        mNotesListActivityRule.launchActivity(i);
    }

    private void onAddButtonClick() {

        onView(withId(R.id.add_button)).perform(click());

        // Following happens in another activity.
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_save_button)).check(matches(isDisplayed()));

        onView(withId(R.id.note_content_title_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.note_content_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.note_title_edit_text)).check(matches(isDisplayed()));

    }

    @Test
    public void onAddButtonClickTest() throws Exception {

        onAddButtonClick();

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

    }

}
