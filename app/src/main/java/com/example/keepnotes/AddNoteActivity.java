package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.keepnotes.databases.AppDatabase;
import com.example.keepnotes.databases.NotesEntry;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.skydoves.transformationlayout.TransformationAppCompatActivity;
import com.skydoves.transformationlayout.TransformationCompat;
import com.skydoves.transformationlayout.TransformationLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mNoteEditText;
    private AppDatabase mDb;
    private Uri mImageUri = null;
    private ImageView mImageNote;
    private String mImageString = null;
    private TextView mDateTimeTextView;
    private boolean mNoteHasChanged = false;
    private String mNoteText = null;
    private ImageButton mDrawBtn = null;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mNoteHasChanged = true;
            return false;
        }
    };

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    private int mTaskId = DEFAULT_TASK_ID;

    private final int TAKE_PHOTO_ID = 1;
    private final int ADD_IMAGE_ID = 2;
    private BottomAppBar mBottomAppBar;
    private String dateFormat;
    private Date date;

    private NotesEntry intentNotesEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        TransformationLayout.Params params = getIntent().getParcelableExtra("TransformationParams");
//        TransformationCompat.onTransformationEndContainer(this, params);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
//        getSupportActionBar().setCustomView(R.layout.back_btn);
//        getSupportActionBar().setCustomView(R.layout.copy_btn);


        mDb = AppDatabase.getInstance(getApplicationContext());

        initViews();
        mDrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNoteActivity.this, CreateDrawing.class);
                startActivity(intent);
            }
        });
        mTitleEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);
        mImageNote.setOnTouchListener(mTouchListener);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            setTitle(getString(R.string.update_note));
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDb, mTaskId);
                final AddNoteViewModel viewModel = new ViewModelProvider(this, factory).get(AddNoteViewModel.class);
                viewModel.getNote().observe(this, new Observer<NotesEntry>() {
                    @Override
                    public void onChanged(NotesEntry notesEntry) {
                        intentNotesEntry = notesEntry;
                        viewModel.getNote().removeObserver(this);
                        populateUI(notesEntry);
                    }
                });
            }
        }
        date = new Date();
        dateFormatter(date);
        mDateTimeTextView.setText("Edited " + dateFormat);

//        mNoteEditText.requestFocus();
        mTitleEditText.setTextIsSelectable(true);
        mTitleEditText.setFocusableInTouchMode(true);
        mNoteEditText.setTextIsSelectable(true);
        mNoteEditText.setFocusableInTouchMode(true);

        ImageView plusImage = findViewById(R.id.plus_image);

        mBottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_note:
                        deleteNote();
                        return true;
                    case R.id.make_copy_note:
                        makeCopy();
                        return true;
                    case R.id.share_note:
                        String note = mNoteEditText.getText().toString().trim();
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, note);
//                        sendIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
//                        sendIntent.setType("image/jpeg");
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, null));
                        return true;
                }
                return true;
            }
        });

        plusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddNoteActivity.this, plusImage);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.plus_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.add(0, TAKE_PHOTO_ID, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_outline_camera_alt_24), getResources().getString(R.string.action_take_photo)));
                menu.add(0, ADD_IMAGE_ID, 2, menuIconWithText(getResources().getDrawable(R.drawable.ic_outline_insert_photo_24), getResources().getString(R.string.action_add_image)));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case TAKE_PHOTO_ID:
                                ImagePicker.Companion.with(AddNoteActivity.this)
                                        .cameraOnly()
                                        .crop()
                                        .compress(2048)
                                        .maxResultSize(1080, 1080)
                                        .start();
                                return true;
                            case ADD_IMAGE_ID:
                                ImagePicker.Companion.with(AddNoteActivity.this)
                                        .galleryOnly()
                                        .crop()
                                        .compress(2048)
                                        .maxResultSize(1080, 1080)
                                        .start();
                                return true;
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
    }

    private void dateFormatter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE hh:mma MMM d, yyyy");
        dateFormat = formatter.format(date);
        try {
            dateFormat = NotesAdapter.formatToYesterdayOrToday(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateDate() {
        mDateTimeTextView.setText("hello");
    }

    private void makeCopy() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    finish();
                } else {
                    NotesEntry newNote = new NotesEntry(intentNotesEntry.getTitle(), intentNotesEntry.getNote(), intentNotesEntry.getEditedAt(), intentNotesEntry.getImageUri());
                    mDb.notesDao().insertNote(newNote);
                    Toast.makeText(AddNoteActivity.this, "Copy Successfully Created", Toast.LENGTH_SHORT).show();
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTaskId == DEFAULT_TASK_ID) {
                            Toast.makeText(AddNoteActivity.this, "Cant make copy of unsaved notes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void populateUI(NotesEntry note) {
        mNoteText = note.getNote();
        mTitleEditText.setText(note.getTitle());
        mNoteEditText.setText(mNoteText);
        date = note.getEditedAt();

        dateFormatter(date);
        mDateTimeTextView.setText("Edited " + dateFormat);
        if (note.getImageUri() != null) {
            mImageNote.setVisibility(View.VISIBLE);
            mImageString = note.getImageUri();
            mImageUri = Uri.parse(mImageString);
            mImageNote.setImageURI(mImageUri);
        }
    }

    private void initViews() {
        mTitleEditText = findViewById(R.id.title);
        mNoteEditText = findViewById(R.id.note);
        mImageNote = findViewById(R.id.image_note);
        mBottomAppBar = findViewById(R.id.bottom_bar);
        mDateTimeTextView = findViewById(R.id.note_activity_date);
        mDrawBtn = findViewById(R.id.draw_icon);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.copy_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // for up arrow button
                if (mNoteHasChanged)
                    onSave();
                NavUtils.navigateUpFromSameTask(AddNoteActivity.this);
                Animatoo.animateZoom(this);
                finish();
                return true;
            case R.id.copy_btn:

                ClipboardManager clipboardManager = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("text", mNoteText);
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(AddNoteActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSave() {

        String title = mTitleEditText.getText().toString().trim();
        String note = mNoteEditText.getText().toString().trim();
        Date date = new Date();

        NotesEntry notesEntry = new NotesEntry(title, note, date, mImageString);

        if ((title.length() == 0 || title == null) && (note != null && note.length() != 0)) {
            notesEntry.setTitle(note);
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    //Insert new note
                    if ((title == null || title.length() == 0) && (note == null || note.length() == 0)) {
                    } else {
                        mDb.notesDao().insertNote(notesEntry);
                    }
                } else {
                    //Update note
                    System.out.println("On update called");
                    notesEntry.setId(mTaskId);
                    mDb.notesDao().updateNote(notesEntry);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTaskId == DEFAULT_TASK_ID) {
                            if ((title == null || title.length() == 0) && (note == null || note.length() == 0)) {
                                Toast.makeText(AddNoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mNoteHasChanged) {
            System.out.println("Note changed : " + mNoteHasChanged);
            onSave();
        }
        Animatoo.animateZoom(this);
        finish();
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageUri = data.getData();
        if (mImageUri != null) {
            mImageString = mImageUri.toString();
            mImageNote.setVisibility(View.VISIBLE);
            mImageNote.setImageURI(mImageUri);
        }
    }

    private void deleteNote() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    finish();
                } else {
                    mDb.notesDao().deleteNote(intentNotesEntry);
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTaskId == DEFAULT_TASK_ID) {
                            Toast.makeText(AddNoteActivity.this, "Cant delete unsaved notes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}


