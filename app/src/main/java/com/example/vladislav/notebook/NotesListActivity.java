package com.example.vladislav.notebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class NotesListActivity extends AppCompatActivity {

    private EditText editText = null;
    private ImageButton searchButton = null;
    private ImageButton addButton = null;
    private ImageButton commitSearchButton = null;
    private List<Note> mNotesList = new LinkedList<>();
    private RecyclerViewAdapter mAdapter;
    private boolean search = false;

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case (R.id.add_button): {
                    editText.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    search = false;
                    Intent intent = NoteActivity.newIntent(NotesListActivity.this);
                    startActivityForResult(intent, 1);
                    break;
                }
                case (R.id.search_button): {
                    if (!search) {
                        editText.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.GONE);
                        commitSearchButton.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.GONE);
                        search = true;
                    } else {
                        editText.setVisibility(View.GONE);
                        searchButton.setVisibility(View.VISIBLE);
                        commitSearchButton.setVisibility(View.GONE);
                        addButton.setVisibility(View.VISIBLE);
                        search = false;
                    }
                    break;
                }
                case (R.id.delete_button): {
                    if (search) {
                        editText.setVisibility(View.GONE);
                        searchButton.setVisibility(View.VISIBLE);
                        commitSearchButton.setVisibility(View.GONE);
                        addButton.setVisibility(View.VISIBLE);
                        search = false;
                    }
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
        editText = (EditText) findViewById(R.id.search_edit_text);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        addButton = (ImageButton) findViewById(R.id.add_button);
        commitSearchButton = (ImageButton) findViewById(R.id.commit_search_button);
        addButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Loading notes from a database.
        try {
            mNotesList = ((LinkedList) DBHelper.getInstance().loadAllNotesFromDataBase());
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

        // Showing a message that notes list is empty when it is so.
        TextView textView = (TextView) findViewById(R.id.recycler_empty_list_text_view);
        if (mNotesList.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

        setupRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DBHelper.NEW_NOTE_ADDED) {
            // Loading notes from a database.
            try {
                mNotesList = ((LinkedList) DBHelper.getInstance().loadAllNotesFromDataBase());
            } catch (ParseException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            mAdapter.update(mNotesList);
        }
    }

    @Override
    public void onBackPressed() {
        if (search) {
            editText.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
            commitSearchButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            search = false;
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
    }

    private void setupRecyclerView() {

        RecyclerView recyclerView = null;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.addOnItemTouchListener(createRecyclerItemClickListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter();
        mAdapter.update(mNotesList);
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

}