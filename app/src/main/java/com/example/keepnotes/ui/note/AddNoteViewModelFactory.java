package com.example.keepnotes.ui.note;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.keepnotes.data.database.AppDatabase;
import com.example.keepnotes.data.repository.NotesRepository;
import com.example.keepnotes.utils.AppExecutors;

import org.jetbrains.annotations.NotNull;

public class AddNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final NotesRepository mRepository;
    private final int mTaskId;

    public AddNoteViewModelFactory(NotesRepository repository, int taskId) {
        this.mRepository = repository;
        this.mTaskId = taskId;
    }

    public AddNoteViewModelFactory(AppDatabase mDb, int mTaskId) {
        this(NotesRepository.getInstance(mDb, AppExecutors.getInstance()), mTaskId);
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AddNoteViewModel(mRepository, mTaskId);
    }
}
