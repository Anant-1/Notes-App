package com.example.keepnotes.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDao {

    @Query("Select * from notes ORDER BY editedAt desc")
    LiveData<List<NotesEntry>> loadAllNotes();

    @Insert
    void insertNote(NotesEntry notesEntry);

    @Update
    void updateNote(NotesEntry notesEntry);

    @Delete
    void deleteNote(NotesEntry notesEntry);

    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<NotesEntry> loadTaskById(int id);

    @Delete
    void deleteAllNotes(List<NotesEntry> notesEntries);

}
