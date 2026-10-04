package com.example.keepnotes.ui.note;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.keepnotes.data.database.AppDatabase;
import com.example.keepnotes.data.database.NotesEntry;
import com.example.keepnotes.data.repository.NotesRepository;
import com.example.keepnotes.utils.AppExecutors;

public class AddNoteViewModel extends ViewModel {

    private final NotesRepository repository;
    private final LiveData<NotesEntry> note;

    public AddNoteViewModel(NotesRepository repository, int noteId) {
        this.repository = repository;
        this.note = (noteId != -1) ? repository.getNoteById(noteId) : null;
    }

    public AddNoteViewModel(AppDatabase db, int noteId) {
        this(NotesRepository.getInstance(db, AppExecutors.getInstance()), noteId);
    }

    public LiveData<NotesEntry> getNote() {
        return note;
    }

    public void insertNote(NotesEntry note) {
        repository.insertNote(note);
    }

    public void updateNote(NotesEntry note) {
        repository.updateNote(note);
    }

    public void deleteNote(NotesEntry note) {
        repository.deleteNote(note);
    }
}
