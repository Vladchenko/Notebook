package com.example.vladislav.notebook;

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

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements RecyclerViewListener {

    private EditText searchEditText = null;
    private ImageButton searchButton = null;
    private ImageButton addButton = null;
    private ImageButton commitOperationButton = null;
    private List<Note> mNotesList = new LinkedList<>();
    private RecyclerViewAdapter mAdapter;
    private boolean searched = false;
    private boolean delete = false;


    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case (R.id.add_button): {
                    searchEditText.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    setSearched(false);
                    Intent intent = NoteActivity.newIntent(NotesListActivity.this);
                    startActivityForResult(intent, 1);
                    break;
                }
                case (R.id.search_button): {
                    if (!isSearched()) {
                        addButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        searchEditText.setVisibility(View.VISIBLE);
                        commitOperationButton.setVisibility(View.VISIBLE);

                        setSearched(true);
                    } else {
                        searchEditText.setVisibility(View.GONE);
                        commitOperationButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.VISIBLE);
                        notifyIfListEmpty();
                        setSearched(false);
                    }
                    break;
                }
                case (R.id.delete_button): {
                    if (isSearched()
                            || delete) {
                        searchEditText.setVisibility(View.GONE);
                        commitOperationButton.setVisibility(View.GONE);
                        addButton.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        setSearched(false);
                        mAdapter.setDeletionCheckBoxVisibility(false);
                        mAdapter.notifyDataSetChanged();
                        delete = false;
                        loadNotesListAndUpdateAdapter(null);
                    } else {
                        // TODO Implement deletion of a note
                        delete = true;
                        mAdapter.setDeletionCheckBoxVisibility(true);
                        mAdapter.notifyDataSetChanged();
                        searchEditText.setVisibility(View.GONE);
                        addButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        commitOperationButton.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case (R.id.commit_operation_button): {
                    if (delete) {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (mNotesList.get(i).isDelete()) {
//                                mAdapter.getItemId(i);
                                System.out.println(mNotesList.get(i).getTitle());
                            }
                        }
                    }
                    loadNotesListAndUpdateAdapter(searchEditText.getText().toString());
                    break;
                }
            }
        }
    };

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NotesListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list_activity);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        addButton = (ImageButton) findViewById(R.id.add_button);
        commitOperationButton = (ImageButton) findViewById(R.id.commit_operation_button);
        mAdapter = new RecyclerViewAdapter(this);
        addButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Loading notes from a database, sending it to adapter and notifying user if list is empty.
        loadNotesListAndUpdateAdapter(null);

        setupRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DBHelper.NEW_NOTE_ADDED) {
            // Loading notes from a database.
            loadNotesListAndUpdateAdapter(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSearched()
                || delete) {
            searchEditText.setVisibility(View.GONE);
            commitOperationButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            setSearched(false);
            mAdapter.setDeletionCheckBoxVisibility(false);
            mAdapter.notifyDataSetChanged();
            delete = false;
            loadNotesListAndUpdateAdapter(null);
        } else {
            super.onBackPressed();
        }
    }

    private void addButtonListeners() {

        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(this.mClickListener);
        ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this.mClickListener);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this.mClickListener);
        ImageButton commitSearchButton = (ImageButton) findViewById(R.id.commit_operation_button);
        commitSearchButton.setOnClickListener(this.mClickListener);

        searchEditText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    loadNotesListAndUpdateAdapter(searchEditText.getText().toString());
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

    private void loadNotesListAndUpdateAdapter(String searchCriterion) {
        try {
            mNotesList = DBHelper.getInstance().loadNotesFromDataBase(
                    searchCriterion);
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
        notifyIfListEmpty();
        mAdapter.update(mNotesList);
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

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

    @Override
    public void onCheckDelete(int position) {
        if (mNotesList.get(position).isDelete()) {
            mNotesList.get(position).setDelete(false);
        } else {
            mNotesList.get(position).setDelete(true);
        }
    }
}