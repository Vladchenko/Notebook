package com.example.vladislav.notebook;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NOTE_ABSENT = -1;
    private static final int TEXT_ABSENT = 0;

    private long mNoteId = NOTE_ABSENT;
    private EditText noteTitleEditText;
    private EditText noteContentTextEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String noteTitle = null;
        String noteContentText = null;
        int noteListPosition = NOTE_ABSENT;

        ImageButton button = null;
        Intent intent = getIntent();
        setContentView(R.layout.note_activity);

        button = (ImageButton) findViewById(R.id.save_button);
        button.setOnClickListener(this);
        button = (ImageButton) findViewById(R.id.cancel_button);
        button.setOnClickListener(this);
        noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
        noteContentTextEditText = (EditText) findViewById(R.id.note_content_edittext);

        mNoteId = intent.getLongExtra(DBNotesContract.Note._ID, NOTE_ABSENT);
        // Present Id says that there is a note to be edited.
        if (mNoteId != NOTE_ABSENT) {
            noteTitle = intent.getStringExtra(DBNotesContract.Note.TITLE);
            noteTitleEditText.setText(noteTitle);
            noteContentText = intent.getStringExtra(DBNotesContract.Note.TEXT);
            noteContentTextEditText.setText(noteContentText);
//            noteListPosition = intent.getIntExtra(DBNotesContract.sNoteListPosition, -1);
        }
    }

    @Override
    public void onClick(View v) {
        Note note;
        java.util.Date currentDateTime = new java.util.Date();
        switch (v.getId()) {
            case R.id.save_button: {
                noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
                if (noteTitleEditText.getText().length() == TEXT_ABSENT) {
                    showAlertDialog("Input a title for your note");
                    break;
                }
                noteContentTextEditText= (EditText) findViewById(R.id.note_content_edittext);
                if (noteContentTextEditText.getText().length() == TEXT_ABSENT) {
                    showAlertDialog("Input a text for your note");
                    break;
                }
                // If note id present, it means user calls this note for editing, so make an update,
                // when editing is done (checkmark is clicked).
                if (mNoteId != NOTE_ABSENT) {
                    ContentValues cv = new ContentValues();
                    SimpleDateFormat sdf = new SimpleDateFormat(Environment.DATE_TIME_FORMAT);
                    cv.put(DBNotesContract.Note.TITLE, noteTitleEditText.getText().toString());
                    cv.put(DBNotesContract.Note.TEXT, noteContentTextEditText.getText().toString());
                    cv.put(DBNotesContract.Note.MODIFICATION_DATE, sdf.format(new Date()));
                    DBHelper.getInstance().getWritableDatabase().update(
                            DBNotesContract.Note.TABLE_NAME,
                            cv,
                            DBNotesContract.Note._ID + "=" + mNoteId,
                            null);
                    Log.i(getClass().getSimpleName(), "Note with id = " + mNoteId + " is updated.");
                } else {
                    // Populating a note bean for further saving it to data base.
                    note = new Note(
                            noteTitleEditText.getText().toString(),
                            noteContentTextEditText.getText().toString(),
                            "",
                            new Date(currentDateTime.getTime()),
                            new Date(currentDateTime.getTime())
                    );
                    // Saving a note to a database.
                    DBHelper.getInstance().getWritableDatabase().insert(
                            DBNotesContract.Note.TABLE_NAME,
                            null,
                            DBHelper.getInstance().setNoteValues(note));
                    setResult(DBHelper.NEW_NOTE_ADDED);
                    Log.i(getClass().getSimpleName(), "Note titled " + noteTitleEditText.getText().toString()
                            + " has been saved to a database.");
                }
                finish();
                break;
            }
            case R.id.cancel_button: {
                setResult(DBHelper.NEW_NOTE_NOT_ADDED);
                finish();
                break;
            }
            // TODO Implement this case
//            case R.id.delete_button: {
//
//            }
        }
    }

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

}
