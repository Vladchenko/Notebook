package com.example.vladislav.notebook.noteslist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vladislav.notebook.NoteActivity;
import com.example.vladislav.notebook.R;
import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerViewListener {

    private EditText mSearchEditText = null;
    private ImageButton mSearchButton = null;
    private ImageButton mAddButton = null;
    private ImageButton mDeleteButton = null;
    // Button which function depends on an operation that is being performed at the moment
    // - deletion or an editing.
    private ImageButton mCommitOperationButton = null;
    private List<Note> mNotesList = new LinkedList<>();
    private RecyclerViewAdapter mAdapter;
    // Flag that says if a search of a note(s) is being performed at the moment.
    private boolean mSearched = false;
    // Flag that says if a deletion of a note is being performed right now.
    private boolean mDelete = false;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NotesListActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (isSearched()
                || mDelete) {
            mSearchEditText.setVisibility(View.GONE);
            mCommitOperationButton.setVisibility(View.GONE);
            mAddButton.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            setSearched(false);
            mAdapter.setDeletionCheckBoxVisibile(false);
            mAdapter.notifyDataSetChanged();
            mDelete = false;
            loadNotesList(null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When new note is added, one has to update the current list of a notes, by
        if (resultCode == DBHelper.NEW_NOTE_ADDED) {
            // loading all notes from a database.
            loadNotesList(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list_activity);
        mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mAddButton = (ImageButton) findViewById(R.id.add_button);
        mDeleteButton = (ImageButton) findViewById(R.id.delete_button);
        mCommitOperationButton = (ImageButton) findViewById(R.id.commit_operation_button);
        mAdapter = new RecyclerViewAdapter(this);
        addButtonsListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Loading all notes from a database, sending it to adapter and notifying user if list is
        // empty.
        loadNotesList(null);

        setupRecyclerView();

    }

    private void addButtonsListeners() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearched()) {
                    mAddButton.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mSearchEditText.setVisibility(View.VISIBLE);
                    mCommitOperationButton.setVisibility(View.VISIBLE);
                    setSearched(true);
                } else {
                    mSearchEditText.setVisibility(View.GONE);
                    mCommitOperationButton.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.VISIBLE);
                    notifyIfListEmpty();
                    setSearched(false);
                }
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setVisibility(View.GONE);
                mSearchButton.setVisibility(View.VISIBLE);
                setSearched(false);
                Intent intent = NoteActivity.newIntent(NotesListActivity.this);
                startActivityForResult(intent, 1);
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearched()
                        || mDelete) {
                    mSearchEditText.setVisibility(View.GONE);
                    mCommitOperationButton.setVisibility(View.GONE);
                    mAddButton.setVisibility(View.VISIBLE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    setSearched(false);
                    mAdapter.setDeletionCheckBoxVisibile(false);
                    mAdapter.notifyDataSetChanged();
                    mDelete = false;
                    loadNotesList(null);
                } else {
                    mDelete = true;
                    mAdapter.setDeletionCheckBoxVisibile(true);
                    mAdapter.notifyDataSetChanged();
                    mSearchEditText.setVisibility(View.GONE);
                    mAddButton.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mCommitOperationButton.setVisibility(View.VISIBLE);
                }
            }
        });
        mCommitOperationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelete) {
                    try {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (mNotesList.get(i).isDelete()) {
                                DBHelper.getInstance().getReadableDatabase().delete(
                                        DBNotesContract.Note.TABLE_NAME,
                                        DBNotesContract.Note._ID + " = \""
                                                + mNotesList.get(i).getID() + "\"",
                                        null
                                );
                            }
                        }
                    } finally {
                        DBHelper.getInstance().close();
                    }
                }
                loadNotesList(mSearchEditText.getText().toString());
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearched()) {
                    mAddButton.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mSearchEditText.setVisibility(View.VISIBLE);
                    mCommitOperationButton.setVisibility(View.VISIBLE);
                    setSearched(true);
                } else {
                    mSearchEditText.setVisibility(View.GONE);
                    mCommitOperationButton.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.VISIBLE);
                    notifyIfListEmpty();
                    setSearched(false);
                }
            }
        });
        mSearchEditText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        // Figuring if an "Enter" button has been pushed to commit a search.
        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    loadNotesList(mSearchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }

    private void setupRecyclerView() {

        RecyclerView recyclerView = null;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

    }

    /**
     * Making a recycler item click listener so that it could put a new note data to an intent to
     * further pass it to NoteActivity for editing.
     */
    private RecyclerItemClickListener createRecyclerItemClickListener() {
        return new RecyclerItemClickListener(NotesListActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(NotesListActivity.this,
                                NoteActivity.class);
                        // Putting a data about a clicked note to an intent, to get it later
                        // in a NoteActivity to edit.
                        intent.putExtra(DBNotesContract.Note._ID,
                                mNotesList.get(position).getID());
                        intent.putExtra(DBNotesContract.Note.TITLE,
                                mNotesList.get(position).getTitle());
                        intent.putExtra(DBNotesContract.Note.TEXT,
                                mNotesList.get(position).getText());
                        startActivity(intent);
                    }
                });
    }

    /**
     * Notifying a user if a notes list is empty.
     */
    private void notifyIfListEmpty() {

        // Showing a message that notes list is empty when it is so.
        TextView textView = (TextView) findViewById(R.id.recycler_empty_list_text_view);
        if (mNotesList.isEmpty()) {
            if (isSearched()) {
                textView.setText(getResources().getText(R.string.no_notes_found_message));
            } else {
                textView.setText(getResources().getText(R.string.empty_notes_list_message));
            }
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

    }

    /**
     * Loading all or several notes from a database (in case a search criterion is provided) and
     * updating adapter for recycler viewer with notes.
     */
    private void loadNotesList(String searchCriterion) {
        try {
            try {
                mNotesList = DBHelper.getInstance().loadNotesFromDataBase(
                        searchCriterion);
            } finally {
                DBHelper.getInstance().close();
            }
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
        notifyIfListEmpty();
        mAdapter.update(mNotesList);
    }

    /**
     * When a recyclerview item (note) is clicked, this call back sends data to NoteActivity
     * that represents a list.
     *
     * @param position - index of an item in a notes list.
     */
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this,
                NoteActivity.class);
        // Putting a data about a clicked note to an intent, to get it later
        // in a NoteActivity to edit.
        intent.putExtra(DBNotesContract.Note._ID,
                mNotesList.get(position).getID());
        intent.putExtra(DBNotesContract.Note.TITLE,
                mNotesList.get(position).getTitle());
        intent.putExtra(DBNotesContract.Note.TEXT,
                mNotesList.get(position).getText());
        startActivity(intent);
    }

    /**
     * Switching a note deletion flag (on/off).
     *
     * @param position - index of an item in a notes list.
     */
    @Override
    public void onCheckDelete(int position) {
        if (mNotesList.get(position).isDelete()) {
            mNotesList.get(position).setDelete(false);
        } else {
            mNotesList.get(position).setDelete(true);
        }
    }

    /**
     * @return flag that says if a search of a note(s) is being performed right now.
     */
    public boolean isSearched() {
        return mSearched;
    }

    /**
     * Sets a flag that says if a note(s) search begins.
     *
     * @param searched
     */
    public void setSearched(boolean searched) {
        this.mSearched = searched;
    }
}