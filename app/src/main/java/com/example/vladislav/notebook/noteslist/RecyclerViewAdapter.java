package com.example.vladislav.notebook.noteslist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vladislav.notebook.Consts;
import com.example.vladislav.notebook.NoteActivity;
import com.example.vladislav.notebook.R;
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

    private final int EDIT_MENU_ITEM_ID = 0;
    private final int DELETE_MENU_ITEM_ID = 1;
    private boolean mDeletionCheckBoxVisibility;

    private Context mContext;
    private List<Note> mNotesList;
    private DateFormat mDateFormat;
    private RecyclerViewListener mRecyclerListener;

    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
        mRecyclerListener = (RecyclerViewListener) context;
        mDateFormat = new SimpleDateFormat(Consts.DATE_TIME_FORMAT);
    }

    @Override
    public NoteListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        NoteListItemViewHolder viewHolder = new NoteListItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteListItemViewHolder holder, final int position) {
        Note note = mNotesList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mRecyclerListener.onItemClick(position);
            }
        });
        holder.titleTextView.setText(note.getTitle());
        holder.modification_timing_text_view.setText(mDateFormat.format(note.getModificationDate()));
        if (isDeletionCheckBoxVisible()){
            holder.deletionCheckBox.setVisibility(View.VISIBLE);
        }else{
            holder.deletionCheckBox.setVisibility(View.GONE);
        }
        holder.deletionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Marking a note with a deletion flag.
                mRecyclerListener.onCheckDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNotesList != null) {
            return mNotesList.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return mNotesList.get(position).getID();
    }

    /**
     *
     * Updating an adapter's list with a new one.
     *
     * @param list - new list to be placed to an adapter's one.
     */
    public void update(List list) {
        this.mNotesList = list;
        notifyDataSetChanged();
    }

    /**
     *
     * In charge of a logic and outlook of a recycler view item.
     *
     */
    public class NoteListItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener
    {

        CheckBox deletionCheckBox;
        TextView titleTextView;
        TextView modification_timing_text_view;
        // Popup menu click handler.
        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    // When "Edit" popup menu item is clicked.
                    case EDIT_MENU_ITEM_ID: {
                        Intent intent = NoteActivity.newIntent(mContext);
                        intent.putExtra(DBNotesContract.Note._ID,
                                mNotesList.get(getAdapterPosition()).getID());
                        intent.putExtra(DBNotesContract.Note.TITLE,
                                mNotesList.get(getAdapterPosition()).getTitle());
                        intent.putExtra(DBNotesContract.Note.TEXT,
                                mNotesList.get(getAdapterPosition()).getText());
                        mContext.startActivity(intent);
                        break;
                    }
                    // When "Delete" popup menu item is clicked.
                    case DELETE_MENU_ITEM_ID: {
                        // Delete a current note from a database.
                        try {
                            DBHelper.getInstance().getWritableDatabase().delete(
                                    DBNotesContract.Note.TABLE_NAME,
                                    DBNotesContract.Note.TITLE + " = \""
                                            + titleTextView.getText().toString() + "\"",
                                    null);
                        } finally {
                            DBHelper.getInstance().close();
                        }
                        // Loading a notes list again from a database.
                        try {
                            mNotesList = DBHelper.getInstance().loadNotesFromDataBase(null);
                            DBHelper.getInstance().close();
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
            deletionCheckBox = (CheckBox) itemView.findViewById(R.id.deletion_check_box);
            itemView.findViewById(R.id.note_list_item).setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, EDIT_MENU_ITEM_ID, 0, "Edit").setOnMenuItemClickListener(onMenuItemClickListener);
            menu.add(0, DELETE_MENU_ITEM_ID, 0, "Delete").setOnMenuItemClickListener(onMenuItemClickListener);
        }

    }

    /**
     *
     * Retrieves a flag saying if a deletion checkbox is visible for every item of a recyclerview.
     *
     * @return
     */
    public boolean isDeletionCheckBoxVisible() {
        return mDeletionCheckBoxVisibility;
    }

    /**
     *
     * Sets a flag saying if a deletion checkbox is visible for every item of a recyclerview.
     *
     * @return
     */
    public void setDeletionCheckBoxVisibile(boolean deletionCheckBoxVisibility) {
        this.mDeletionCheckBoxVisibility = deletionCheckBoxVisibility;
    }

    interface RecyclerViewListener {

        // Triggers when a recyclerview item is clicked.
        void onItemClick(int position);

        // Triggers when a delete checkmark in a recyclerview item is clicked.
        void onCheckDelete(int position);

    }

}