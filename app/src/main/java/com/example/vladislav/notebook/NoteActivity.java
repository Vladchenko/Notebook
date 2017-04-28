package com.example.vladislav.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    // Says that there is no mNote in a database
    private static final int NOTE_ABSENT = -1;
    // When there is no text present in a newly adding mNote.
    private static final int TEXT_ABSENT = 0;

    private Note mNote;
    private Date mCurrentDateTime;
    private long mNoteId = NOTE_ABSENT;
    private EditText mNoteTitleEditText;
    private EditText mNoteContentTextEditText;

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.save_button: {
                    mNoteTitleEditText = (EditText) findViewById(R.id.note_title_edit_text);
                    if (mNoteTitleEditText.getText().length() == TEXT_ABSENT) {
                        showAlertDialog(getResources().getString(R.string.new_note_title_missing_message));
                        break;
                    }
                    mNoteContentTextEditText = (EditText) findViewById(R.id.note_content_edit_text);
                    if (mNoteContentTextEditText.getText().length() == TEXT_ABSENT) {
                        showAlertDialog(getResources().getString(R.string.new_note_text_missing_message));
                        break;
                    }
                    // If mNote id present, it means user calls this mNote for editing,
                    // so make an update, when editing is done (checkmark is clicked in activity).
                    try {
                        updateOrAddNote();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                }
                case R.id.cancel_save_button: {
                    setResult(DBHelper.NEW_NOTE_NOT_ADDED);
                    finish();
                    break;
                }
            }
        }
    };

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_activity);

        String noteTitle = null;
        String noteContentText = null;
        Intent intent = getIntent();

        setButtonsListeners();

        mNoteId = intent.getLongExtra(DBNotesContract.Note._ID, NOTE_ABSENT);
        // Present Id says that there is a mNote in a database to be edited.
        if (mNoteId != NOTE_ABSENT) {
            noteTitle = intent.getStringExtra(DBNotesContract.Note.TITLE);
            mNoteTitleEditText.setText(noteTitle);
            noteContentText = intent.getStringExtra(DBNotesContract.Note.TEXT);
            mNoteContentTextEditText.setText(noteContentText);
        }
    }

    // Making up a ContentValues for further putting them to a database.
    private ContentValues assignNoteContentValues() {
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);
        contentValues.put(DBNotesContract.Note.TITLE, mNoteTitleEditText.getText().toString());
        contentValues.put(DBNotesContract.Note.TEXT, mNoteContentTextEditText.getText().toString());
        contentValues.put(DBNotesContract.Note.MODIFICATION_DATE, sdf.format(new Date()));
        return contentValues;
    }

    // Updating an existing note or adding a new one to a database.
    private void updateOrAddNote() throws ParseException {

        if (mNoteId != NOTE_ABSENT) {

            // Updating an existing mNote.
            try {
                DBHelper.getInstance().getWritableDatabase().update(
                        DBNotesContract.Note.TABLE_NAME,
                        assignNoteContentValues(),
                        DBNotesContract.Note._ID + "=" + mNoteId,
                        null);
            } finally {
                DBHelper.getInstance().close();
            }
            Log.i(getClass().getSimpleName(), "Note with id = " + mNoteId + " is updated.");

        } else {

            // Checking if a note with such a title exists and if so - do not add it and inform
            // a user.
            List<Note> notesList;
            try {
                notesList = DBHelper.getInstance().loadNotesFromDataBase(
                        mNoteTitleEditText.getText().toString());
            } finally {
                DBHelper.getInstance().close();
            }

            // If notesList is not empty, that means note with such title, already exists.
            if (!notesList.isEmpty()) {
                Toast.makeText(this, getResources().getText(R.string.note_exists_message),
                        Toast.LENGTH_SHORT).show();
                Log.i(getClass().getSimpleName(), "Note titled "
                        + mNoteTitleEditText.getText().toString()
                        + " exists in a database.");
            } else {
                addNewNote();
                Log.i(getClass().getSimpleName(), "Note titled "
                        + mNoteTitleEditText.getText().toString()
                        + " is added to a database.");
            }

        }

    }

    // Assigning a click listeners to all the buttons present on an activity.
    private void setButtonsListeners() {
        ImageButton button = null;
        button = (ImageButton) findViewById(R.id.save_button);
        button.setOnClickListener(this.mClickListener);
        button = (ImageButton) findViewById(R.id.cancel_save_button);
        button.setOnClickListener(this.mClickListener);
    }

    // Notifying a user when something is wrong.
    private void showAlertDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setNeutralButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertdialog = builder.create();
        alertdialog.show();

    }

    // Putting a new Note to a database.
    private void addNewNote() {
        mCurrentDateTime = new Date();
        // Populating a mNote bean for further saving it to a data base.
        mNote = new Note(
                mNoteTitleEditText.getText().toString(),
                mNoteContentTextEditText.getText().toString(),
                "",
                mCurrentDateTime,
                mCurrentDateTime
        );
        // Saving a mNote to a database.
        try {
            DBHelper.getInstance().getWritableDatabase().insert(
                    DBNotesContract.Note.TABLE_NAME,
                    null,
                    DBHelper.getInstance().setNoteValues(mNote));
        } finally {
            DBHelper.getInstance().close();
        }
        setResult(DBHelper.NEW_NOTE_ADDED);
    }

}
