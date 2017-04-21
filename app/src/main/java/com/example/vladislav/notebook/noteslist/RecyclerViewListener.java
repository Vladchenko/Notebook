package com.example.vladislav.notebook.noteslist;

/**
 * Created by vladislav on 18.04.17.
 */

public interface RecyclerViewListener {

    // Triggers when a recyclerview item is clicked.
    void onItemClick(int position);

    // Triggers when a delete checkmark in a recyclerview item is clicked.
    void onCheckDelete(int position);

}
