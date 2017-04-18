package com.example.vladislav.notebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vladislav.notebook.bean.Note;
import com.example.vladislav.notebook.database.DBHelper;
import com.example.vladislav.notebook.database.DBNotesContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by vladislav on 06.02.17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NoteListItemViewHolder> {

    private Context context;
    // Position of a clicked item of a notes list in a recyclerview.
    private int position = -1;
    private List<Note> mNotesList;
    private DateFormat dateFormat = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);
    private final int editMenuItemId = 604049931;
    private final int deleteMenuItemId = 639592949;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NoteListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        NoteListItemViewHolder viewHolder = new NoteListItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteListItemViewHolder holder, int position) {
        Note note = mNotesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.modification_timing_text_view.setText(dateFormat.format(note.getModificationDate()));
    }

    @Override
    public int getItemCount() {
        if (mNotesList != null) {
            return mNotesList.size();
        } else {
            return 0;
        }
    }

    public void update(List list) {
        this.mNotesList = list;
        notifyDataSetChanged();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public class NoteListItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener
    {

        TextView titleTextView;
        TextView modification_timing_text_view;
        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                System.out.println(item.getItemId());
                switch (item.getItemId()) {
                    case editMenuItemId: {
                        Intent intent = NoteActivity.newIntent(context);
                        intent.putExtra(DBNotesContract.Note._ID,
                                mNotesList.get(getAdapterPosition()).getID());
                        intent.putExtra(DBNotesContract.Note.TITLE,
                                mNotesList.get(getAdapterPosition()).getTitle());
                        intent.putExtra(DBNotesContract.Note.TEXT,
                                mNotesList.get(getAdapterPosition()).getText());
                        context.startActivity(intent);
                        break;
                    }
                    case deleteMenuItemId: {
                        // Delete a current note from a database.
                        DBHelper.getInstance().getWritableDatabase().delete(
                                DBNotesContract.Note.TABLE_NAME,
                                DBNotesContract.Note.TITLE + " = \""
                                        + titleTextView.getText().toString() + "\"",
                                null);
                        // Loading a notes list again from a database.
                        try {
                            mNotesList = DBHelper.getInstance().loadNotesFromDataBase(null);
                        } catch (ParseException e) {
                            Log.e(getClass().getSimpleName(), e.getMessage());
                        }
                        // Updating a list.
                        update(mNotesList);
                        break;
                    }
                }
                return true;
            }
        };

        public NoteListItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.note_title_text_view);
            modification_timing_text_view = (TextView) itemView.findViewById(R.id.modification_timing_text_view);
            itemView.findViewById(R.id.note_list_item).setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, editMenuItemId, 0, "Edit").setOnMenuItemClickListener(onMenuItemClickListener);
            menu.add(0, deleteMenuItemId, 0, "Delete").setOnMenuItemClickListener(onMenuItemClickListener);
        }

    }

}