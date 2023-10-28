package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.keepnotes.databases.AppDatabase;
import com.example.keepnotes.databases.NotesEntry;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import static com.skydoves.transformationlayout.TransformationCompat.onTransformationStartContainer;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private ImageView accountImage;
    private SharedPreferences sharedPreferences;
    private String userId;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private ImageView searchImageView;
    private ConstraintLayout searchView;
    private Toast searchToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        System.out.println("Main activity called");
        mDb = AppDatabase.getInstance(getApplicationContext());
        db = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_linear);
        mRecyclerView = findViewById(R.id.recyclerView);
        frameLayout = findViewById(R.id.frame_layout);
        searchImageView = findViewById(R.id.search_img_view);

        progressBar.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        searchImageView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);

        emptyView = findViewById(R.id.empty_notes);
        emptyView2 = findViewById(R.id.empty_notes_text);
        gridImageView = findViewById(R.id.grid_view);
        mBottomAppBar = findViewById(R.id.bottomAppbar);
        accountImage = findViewById(R.id.account_main_img);
        bindImage(accountImage);
        searchToast = Toast.makeText(this, "No notes found", Toast.LENGTH_SHORT);
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });

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
//                System.out.println("after text changed");
                filter(s.toString());
            }
        });
        setupViewModel();
    }

    private void filter(String text) {
//        System.out.println("filter in main activity");
        ArrayList<NotesEntry> filteredNotes = new ArrayList<>();
        for(NotesEntry item : mNotesEntries) {
            if(item.getNote().toLowerCase().contains(text.toLowerCase()) || item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredNotes.add(item);
            }
        }
        if(filteredNotes.isEmpty()) {
            if(searchToast != null) {
                searchToast.cancel();
            }
            searchToast.show();
        }
        mAdapter.filterList(filteredNotes);
    }

    private void deleteAllNotes() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().deleteAllNotes(mNotesEntries);
                db.getReference().child(getString(R.string.firebase_users)).child(userId).child(getString(R.string.firebase_notes)).setValue(null);
            }

        });
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm the action");
        builder.setMessage("Do you really want to delete all notes?");
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.yellow_color_of_fab));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.yellow_color_of_fab));
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
                if(notesEntries == null || notesEntries.isEmpty()) {
                    reference = db.getReference().child(getString(R.string.firebase_users)).child(userId).child(getString(R.string.firebase_notes));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren() && snapshot.exists()) {
                                System.out.println("t- children true");
                                for(DataSnapshot ds : snapshot.getChildren()) {
                                    NotesEntry notesEntry = createNoteFromSnapshot(ds);
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDb.notesDao().insertNote(notesEntry);
                                        }
                                    });

                                }
                            }else{
                                setData(notesEntries);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(notesEntries != null && notesEntries.size() != 0) {
                    System.out.println("t- size1 : " + notesEntries.size());
                    NotesEntry notesEntry = notesEntries.get(0);
                    saveNoteToFirebase(notesEntry);
                    setData(notesEntries);
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

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    protected void bindImage(ImageView imgView) {
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        userId = sharedPreferences.getString(getString(R.string.key_user_id), "");
        reference = db.getReference().child(getString(R.string.firebase_users)).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String profileUrl = snapshot.child("profile").getValue().toString();
                if(snapshot.exists()) {
                    Picasso.get()
                            .load(profileUrl)
//                            .resize(100, 100)
                            .transform(new CropCircleTransformation()).into(imgView);
                    progressBar.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                    searchImageView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveNoteToFirebase(NotesEntry notesEntry) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                reference = db.getReference().child(getString(R.string.firebase_users)).child(userId).child(getString(R.string.firebase_notes)).child(Integer.toString(notesEntry.getId()));;
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            reference.setValue(notesEntry);
                        }
                     }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private static NotesEntry createNoteFromSnapshot(@NonNull DataSnapshot snapshot) {
        String noteString = snapshot.getValue().toString();
        NotesEntry notesEntry = null;
        try {
            JSONObject noteJson = new JSONObject(noteString);
            String note = noteJson.getString("note");
            String title = noteJson.getString("title");
            int id = noteJson.getInt("id");
            String imageUri = noteJson.has("imageUri") ? noteJson.getString("imageUri") : null;
            JSONObject editedAt = noteJson.getJSONObject("editedAt");
            Timestamp ts=new Timestamp(editedAt.getLong("time"));
            Date date=new Date(ts.getTime());
            notesEntry = new NotesEntry(id, title, note, date, imageUri);

        } catch (Exception e) {
            Log.d("Parse Failed", e.toString());
        }
        return notesEntry;
    }
    private void setData(List<NotesEntry> notesEntries) {
        mAdapter.setNotes(notesEntries);
        mNotesEntries = notesEntries;

        if(mNotesEntries == null || mNotesEntries.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            emptyView2.setVisibility(View.GONE);
        }
    }
}