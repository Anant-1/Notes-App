package com.example.keepnotes.databases;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes")
public class NotesEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String note;
    private Date editedAt;
    private String imageUri;

    @Ignore
    public NotesEntry(String title, String note, Date editedAt) {
        this.title = title;
        this.note = note;
        this.editedAt = editedAt;
    }

    @Ignore
    public NotesEntry(String title, String note, Date editedAt, String imageUri) {
        this.title = title;
        this.note = note;
        this.editedAt = editedAt;
        this.imageUri = imageUri;
    }

    public NotesEntry(int id, String title, String note, Date editedAt, String imageUri) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.editedAt = editedAt;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
