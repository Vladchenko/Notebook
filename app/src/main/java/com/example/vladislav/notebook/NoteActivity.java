package com.example.vladislav.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

    private static final int NOTE_ABSENT = -1;
    private static final int TEXT_ABSENT = 0;

    private long mNoteId = NOTE_ABSENT;
    private EditText noteTitleEditText;
    private EditText noteContentTextEditText;
    private Note note;
    private Date currentDateTime;

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.save_button: {
                    noteTitleEditText = (EditText) findViewById(R.id.note_title_edit_text);
                    if (noteTitleEditText.getText().length() == TEXT_ABSENT) {
                        showAlertDialog("Input a title for your note");
                        break;
                    }
                    noteContentTextEditText = (EditText) findViewById(R.id.note_content_edit_text);
                    if (noteContentTextEditText.getText().length() == TEXT_ABSENT) {
                        showAlertDialog("Input a text for your note");
                        break;
                    }
                    // If note id present, it means user calls this note for editing,
                    // so make an update, when editing is done (checkmark is clicked in activity).
                    try {
                        updateOrAddNote();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                }
                case R.id.cancel_search_button: {
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
        // Present Id says that there is a note to be edited.
        if (mNoteId != NOTE_ABSENT) {
            noteTitle = intent.getStringExtra(DBNotesContract.Note.TITLE);
            noteTitleEditText.setText(noteTitle);
            noteContentText = intent.getStringExtra(DBNotesContract.Note.TEXT);
            noteContentTextEditText.setText(noteContentText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private ContentValues assignNoteContentValues() {
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);
        contentValues.put(DBNotesContract.Note.TITLE, noteTitleEditText.getText().toString());
        contentValues.put(DBNotesContract.Note.TEXT, noteContentTextEditText.getText().toString());
        contentValues.put(DBNotesContract.Note.MODIFICATION_DATE, sdf.format(new Date()));
        return contentValues;
    }

    private void updateOrAddNote() throws ParseException {

        if (mNoteId != NOTE_ABSENT) {

            // Updating an existing note.
            DBHelper.getInstance().getWritableDatabase().update(
                    DBNotesContract.Note.TABLE_NAME,
                    assignNoteContentValues(),
                    DBNotesContract.Note._ID + "=" + mNoteId,
                    null);
            Log.i(getClass().getSimpleName(), "Note with id = " + mNoteId + " is updated.");

        } else {

            // Checking if a note with such a title exists and if so - do not add it and inform
            // a user.
            List<Note> notesList = DBHelper.getInstance().loadNotesFromDataBase(
                    noteTitleEditText.getText().toString());

            // If notesList is not empty, that means note with such a title already exists.
            if (!notesList.isEmpty()) {
                Toast.makeText(this, getResources().getText(R.string.note_exists_message),
                        Toast.LENGTH_SHORT).show();
                Log.i(getClass().getSimpleName(), "Note titled "
                        + noteTitleEditText.getText().toString()
                        + " exists in a database.");
            } else {
                addNewNote();
                Log.i(getClass().getSimpleName(), "Note titled "
                        + noteTitleEditText.getText().toString()
                        + " is added to a database.");
            }

        }

    }

    private void setButtonsListeners() {
        ImageButton button = null;
        button = (ImageButton) findViewById(R.id.save_button);
        button.setOnClickListener(this.mClickListener);
        button = (ImageButton) findViewById(R.id.cancel_search_button);
        button.setOnClickListener(this.mClickListener);
        noteTitleEditText = (EditText) findViewById(R.id.note_title_edit_text);
        noteContentTextEditText = (EditText) findViewById(R.id.note_content_edit_text);
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

    private void addNewNote() {
        currentDateTime = new Date();
        // Populating a note bean for further saving it to a data base.
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
    }

}
