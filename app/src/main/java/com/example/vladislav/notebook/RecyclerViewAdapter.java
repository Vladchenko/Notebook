package com.example.vladislav.notebook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vladislav.notebook.bean.Note;

import java.util.List;

/**
 * Created by vladislav on 06.02.17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NoteListItemViewHolder> {

    private List<Note> mNotesList;

    public RecyclerViewAdapter() {
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
        holder.titleTextView.setText(note.getmTitle());
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

    public class NoteListItemViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public NoteListItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.note_title_text_view);
        }

    }

}