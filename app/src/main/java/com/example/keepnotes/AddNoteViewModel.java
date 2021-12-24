package com.example.keepnotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.keepnotes.databases.AppDatabase;
import com.example.keepnotes.databases.NotesEntry;

public class AddNoteViewModel extends ViewModel {

    private LiveData<NotesEntry> note;

    public AddNoteViewModel(AppDatabase db, int noteId) {
        note = db.notesDao().loadTaskById(noteId);
    }
    public LiveData<NotesEntry> getNote() {
        return note;
    }
}
