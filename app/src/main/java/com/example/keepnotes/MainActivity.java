package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.keepnotes.databases.AppDatabase;
import com.example.keepnotes.databases.NotesEntry;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skydoves.transformationlayout.TransformationCompat;
import com.skydoves.transformationlayout.TransformationLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.skydoves.transformationlayout.TransformationCompat.onTransformationStartContainer;

public class MainActivity extends AppCompatActivity implements NotesAdapter.ItemClickListener {

    private AppDatabase mDb;
    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private BottomAppBar mBottomAppBar;
    private ImageView gridImageView;
    private boolean hasLayoutChanged = false;
    private List<NotesEntry> mNotesEntries;
    private ImageView emptyView;
    private TextView emptyView2;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        TransformationCompat.onTransformationStartContainer(this);
////        onTransformationStartContainer(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.recyclerView);

        emptyView = findViewById(R.id.empty_notes);

        emptyView2 = findViewById(R.id.empty_notes_text);
//        transformationLayout = findViewById(R.id.transformationLayout);

        gridImageView = findViewById(R.id.grid_view);
        mBottomAppBar = findViewById(R.id.bottomAppbar);

        mBottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_all:
                        showDeleteConfirmationDialog();
                        return true;
                }
                return true;
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        gridImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasLayoutChanged == false) {
//                    mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
//                    gridImageView.setImageResource(R.drawable.ic_baseline_view_agenda_24);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    gridImageView.setImageResource(R.drawable.ic_outline_dashboard_24);
                    hasLayoutChanged = true;
                }
                else if(hasLayoutChanged) {
//                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//                    gridImageView.setImageResource(R.drawable.ic_outline_dashboard_24);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    gridImageView.setImageResource(R.drawable.ic_outline_view_agenda_24);

                    hasLayoutChanged = false;
                }
            }
        });

        mAdapter = new NotesAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
//                AppExecutors.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        int adapterPosition = viewHolder.getAdapterPosition();
//                        List<NotesEntry> notes = mAdapter.getTasks();
//                        mDb.notesDao().deleteNote(notes.get(adapterPosition));
//                    }
//                });
//            }
//        }).attachToRecyclerView(mRecyclerView);

//        Bundle bundle = transformationLayout.withActivity(this, "myTransitionName");
//        Intent intent = new Intent(this, No.class);
//        intent.putExtra("TransformationParams", transformationLayout.getParcelableParams());
//        startActivity(intent, bundle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new intent to start an AddTaskActivity
//                Bundle bundle = transformationLayout.withActivity(MainActivity.this, "myTransitionName");
                Intent addNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
//                addNoteIntent.putExtra("TransformationParams", transformationLayout.getParcelableParams());
                startActivity (addNoteIntent);
                Animatoo.animateZoom(MainActivity.this);
//                TransformationCompat.startActivity(addNoteIntent, bundle);
            }
        });
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("after text changed");
                filter(s.toString());
            }
        });
        setupViewModel();
    }

    private void filter(String text) {
        System.out.println("filter in main activity");
        ArrayList<NotesEntry> filteredNotes = new ArrayList<>();
        for(NotesEntry item : mNotesEntries) {
            if(item.getNote().toLowerCase().contains(text.toLowerCase()) || item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredNotes.add(item);
            }
        }
        mAdapter.filterList(filteredNotes);
    }

    private void deleteAllNotes() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().deleteAllNotes(mNotesEntries);
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete all notes?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllNotes();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getNotes().observe(this, new Observer<List<NotesEntry>>() {
            @Override
            public void onChanged(List<NotesEntry> notesEntries) {
                mAdapter.setNotes(notesEntries);
                mNotesEntries = notesEntries;
                if(mNotesEntries.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView2.setVisibility(View.VISIBLE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    emptyView2.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(AddNoteActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
        Animatoo.animateZoom(MainActivity.this);
    }
}