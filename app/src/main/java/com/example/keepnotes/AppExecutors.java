package com.example.keepnotes;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIo;
    private final Executor mainThread;
    private final Executor networkIo;

    public AppExecutors(Executor diskIo, Executor mainThread, Executor networkIo) {
        this.diskIo = diskIo;
        this.mainThread = mainThread;
        this.networkIo = networkIo;
    }

    public static AppExecutors getInstance() {
        if(sInstance == null) {
            System.out.println("come");
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3), new MainThreadExecutor());
            }
        }else {
            System.out.println("not come");
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIo;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIo;
    }


    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
