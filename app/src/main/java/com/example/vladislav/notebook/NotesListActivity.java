package com.example.vladislav.notebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;
import com.example.vladislav.notebook.logic.NotesHandler;

import java.text.ParseException;
import java.util.LinkedList;

public class NotesListActivity extends AppCompatActivity implements View.OnClickListener {

    private NotesHandler mNotesHandler = new NotesHandler();
    private RecyclerViewAdapter mAdapter = new RecyclerViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_list_activity);
        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
//        RecyclerViewAdapter recyclerViewAdapter;
        RecyclerView recyclerView;

        // Loading notes from a database.
        try {
            mNotesHandler.setNotes((LinkedList)DBHelper.getInstance().loadAllNotesFromDataBase());
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
//        Log.i(getClass().getSimpleName(), "READ TITLE - " + mNotesHandler.getNotes().get(0).getmTitle());

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(NotesListActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
//                                mListener.onBankOfficeSelected(mNotesHandler.getNotes().get(position));
//                                mEditor = mSharedPreferences.edit();
//                                mEditor.putInt(Consts.BANK_LIST_INDEX, position);
//                                mEditor.commit();
                                Intent intent = new Intent(NotesListActivity.this,
                                        AddNoteActivity.class);
                                // Putting a data about a clicked note to an intent, to get it later
                                // in a AddNoteActivity to edit.
                                intent.putExtra(DBNotesContract.Note._ID,
                                        mNotesHandler.getNotes().get(position).getmID());
                                intent.putExtra(DBNotesContract.Note.TITLE,
                                        mNotesHandler.getNotes().get(position).getmTitle());
                                intent.putExtra(DBNotesContract.Note.TEXT,
                                        mNotesHandler.getNotes().get(position).getmText());
                                startActivity(intent);
                            }
                        })
        );
        mAdapter = new RecyclerViewAdapter();
        mAdapter.update(mNotesHandler.getNotes());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.add_button): {
                Intent intent = new Intent(this, AddNoteActivity.class);
                startActivityForResult(intent, 1);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            // Loading notes from a database.
            try {
                mNotesHandler.setNotes((LinkedList)DBHelper.getInstance().loadAllNotesFromDataBase());
            } catch (ParseException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            mAdapter.update(mNotesHandler.getNotes());
        }
    }
}