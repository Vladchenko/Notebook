package com.example.vladislav.notebook.bean;

import java.util.Date;

/**
 * Created by vladislav on 13.04.17.
 * Bean for a Note entity.
 */

public class Note {

    private long mID;                // ID of a current note.
    private String mTitle;           // Title of a note to be created.
    private String mText;            // Text of a note to be created.
    private String mTag;             // Some mTag not sure what for yet.
    private Date mCreationDate;      // Date and time when a note was created.
    private Date mModificationDate;  // Date and time when a note was modified.
    private boolean delete;          // Flag that says if a note is to be deleted.

    public Note() {}

    public Note(String title, String text, String tag, Date сreationDate, Date modificationDate) {
        this.mTitle = title;
        this.mText = text;
        this.mTag = tag;
        this.mCreationDate = сreationDate;
        this.mModificationDate = modificationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (!mTitle.equals(note.mTitle)) return false;
        if (!mText.equals(note.mText)) return false;
        return mTag != null ? mTag.equals(note.mTag) : note.mTag == null;

    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mText.hashCode();
        result = 31 * result + (mTag != null ? mTag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Note{" +
                "mTitle='" + mTitle + '\'' +
                ", mText='" + mText + '\'' +
                ", mTag='" + mTag + '\'' +
                '}';
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    public Date getModificationDate() {
        return mModificationDate;
    }

    public void setModificationDate(Date mModificationDate) {
        this.mModificationDate = mModificationDate;
    }

    public long getID() {
        return mID;
    }

    public void setID(long mID) {
        this.mID = mID;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

}