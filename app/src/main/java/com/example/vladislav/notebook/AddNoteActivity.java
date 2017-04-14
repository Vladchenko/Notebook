package com.example.vladislav.notebook;

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

import java.sql.Date;
import java.util.Random;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long noteId = -1;
        String noteTitle = null;
        String noteContentText = null;
        EditText noteTitleEditText;
        EditText noteContentTextEditText;
        ImageButton button = null;
        Intent intent = getIntent();
        setContentView(R.layout.add_note_activity);

        button = (ImageButton) findViewById(R.id.save_button);
        button.setOnClickListener(this);
        button = (ImageButton) findViewById(R.id.cancel_button);
        button.setOnClickListener(this);
        noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
        noteContentTextEditText = (EditText) findViewById(R.id.note_content_edittext);

        noteId = intent.getLongExtra(DBNotesContract.Note._ID, -1);
        // Present Id says that there is a note to be edited.
        if (noteId != -1) {
            noteTitle = intent.getStringExtra(DBNotesContract.Note.TITLE);
            noteTitleEditText.setText(noteTitle);
            noteContentText = intent.getStringExtra(DBNotesContract.Note.TEXT);
            noteContentTextEditText.setText(noteContentText);
        }
    }

    @Override
    public void onClick(View v) {
        EditText noteTitleEditText = null;
        EditText noteTextEditText = null;
        java.util.Date currentDateTime = new java.util.Date();
        switch (v.getId()) {
            case R.id.save_button: {
                noteTitleEditText = (EditText) findViewById(R.id.note_title_edittext);
                if (noteTitleEditText.getText().length() == 0) {
                    showAlertDialog("Input a title for your note");
                    break;
                }
                noteTextEditText = (EditText) findViewById(R.id.note_content_edittext);
                if (noteTextEditText.getText().length() == 0) {
                    showAlertDialog("Input a text for your note");
                    break;
                }
                // Populating a note bean for further saving it to data base.
                Note note = new Note(
                        new Random().nextInt(),
                        noteTitleEditText.getText().toString(),
                        noteTextEditText.getText().toString(),
                        "",
                        new Date(currentDateTime.getTime()),
                        new Date(currentDateTime.getTime())
                );
                // Saving a note to a database.
                DBHelper.getInstance().getWritableDatabase().insert(
                        DBNotesContract.Note.TABLE_NAME,
                        null,
                        DBHelper.getInstance().setNoteValues(note));
//                showAlertDialog("Note titled " + noteTitleEditText.getText().toString()
//                        + " has been saved to a database.");
                setResult(1);
                Log.i(getClass().getSimpleName(), "Note titled " + noteTitleEditText.getText().toString()
                        + " has been saved to a database.");
                finish();
                break;
            }
            case R.id.cancel_button: {
                setResult(0);
                finish();
                break;
            }
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
