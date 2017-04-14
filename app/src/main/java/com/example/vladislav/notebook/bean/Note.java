package com.example.vladislav.notebook.bean;

import java.util.Date;

/**
 * Created by vladislav on 13.04.17.
 */

public class Note {

    private long mID;                 // ID of a current note.
    private String mTitle;           // Title of a note to be created.
    private String mText;            // Text of a note to be created.
    private String mTag;             // Some mTag not sure what for yet.
    private Date mCreationDate;      // Date and time when a note was created.
    private Date mModificationDate;  // Date and time when a note was modified.

    public Note() {}

    public Note(int mID, String mTitle, String mText, String mTag, Date mCreationDate, Date mModificationDate) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mText = mText;
        this.mTag = mTag;
        this.mCreationDate = mCreationDate;
        this.mModificationDate = mModificationDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public Date getmCreationDate() {
        return mCreationDate;
    }

    public void setmCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    public Date getmModificationDate() {
        return mModificationDate;
    }

    public void setmModificationDate(Date mModificationDate) {
        this.mModificationDate = mModificationDate;
    }

    public long getmID() {
        return mID;
    }

    public void setmID(long mID) {
        this.mID = mID;
    }
}
