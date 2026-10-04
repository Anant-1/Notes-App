package com.example.keepnotes.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.keepnotes.data.database.AppDatabase;
import com.example.keepnotes.data.database.NotesDao;
import com.example.keepnotes.data.database.NotesEntry;
import com.example.keepnotes.utils.AppExecutors;

import java.util.List;

/**
 * Repository pattern implementation to abstract data access from ViewModels and Activities.
 */
public class NotesRepository {

    private static final Object LOCK = new Object();
    private static NotesRepository sInstance;
    private final NotesDao notesDao;
    private final AppExecutors executors;

    public NotesRepository(NotesDao notesDao, AppExecutors executors) {
        this.notesDao = notesDao;
        this.executors = executors;
    }

    public static NotesRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    AppDatabase db = AppDatabase.getInstance(application);
                    sInstance = new NotesRepository(db.notesDao(), AppExecutors.getInstance());
                }
            }
        }
        return sInstance;
    }

    public static NotesRepository getInstance(AppDatabase db, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new NotesRepository(db.notesDao(), executors);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<NotesEntry>> getAllNotes() {
        return notesDao.loadAllNotes();
    }

    public LiveData<NotesEntry> getNoteById(int id) {
        return notesDao.loadTaskById(id);
    }

    public void insertNote(NotesEntry note) {
        executors.diskIO().execute(() -> notesDao.insertNote(note));
    }

    public void updateNote(NotesEntry note) {
        executors.diskIO().execute(() -> notesDao.updateNote(note));
    }

    public void deleteNote(NotesEntry note) {
        executors.diskIO().execute(() -> notesDao.deleteNote(note));
    }

    public void deleteAllNotes(List<NotesEntry> notes) {
        executors.diskIO().execute(() -> notesDao.deleteAllNotes(notes));
    }
}
