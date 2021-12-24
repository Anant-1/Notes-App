package com.example.keepnotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.keepnotes.databases.AppDatabase;
import com.example.keepnotes.databases.NotesEntry;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<NotesEntry>> notes;

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        notes = database.notesDao().loadAllNotes();
        System.out.println("Notes size in main view model class : " + notes);
    }

    public LiveData<List<NotesEntry>> getNotes() { return notes; }


}
