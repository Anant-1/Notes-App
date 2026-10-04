package com.example.keepnotes.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.keepnotes.data.database.NotesEntry;
import com.example.keepnotes.data.repository.NotesRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final NotesRepository repository;
    private final LiveData<List<NotesEntry>> notes;

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = NotesRepository.getInstance(application);
        notes = repository.getAllNotes();
    }

    public LiveData<List<NotesEntry>> getNotes() {
        return notes;
    }

    public void deleteAllNotes(List<NotesEntry> notesList) {
        repository.deleteAllNotes(notesList);
    }

    public void deleteNote(NotesEntry note) {
        repository.deleteNote(note);
    }
}
