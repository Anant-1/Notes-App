package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfilePage extends AppCompatActivity {
    private ImageView profileImgView;
    private TextView profileNameTextView;
    private TextView profileEmailTextView;
    private FirebaseDatabase db;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private Button signOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        profileImgView = findViewById(R.id.profile_img);
        profileNameTextView = findViewById(R.id.profile_name);
        profileEmailTextView = findViewById(R.id.profile_email);
        progressBar = findViewById(R.id.progress_bar);
        signOutButton = findViewById(R.id.btn_signOut);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        bindImage(profileImgView);
    }

    protected void bindImage(ImageView imgView) {

        String userId = sharedPreferences.getString(getString(R.string.key_user_id), "");
        db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child(getString(R.string.firebase_users)).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int w = 250, h = 250;
                String profileUrl = snapshot.child("profile").getValue().toString();
                if(snapshot.exists()) {
                    profileNameTextView.setText(snapshot.child("name").getValue().toString());
                    profileEmailTextView.setText((CharSequence) snapshot.child("email").getValue().toString());
                    Picasso.get()
                            .load(profileUrl)
                            .resize(w, h)
                            .transform(new CropCircleTransformation()).into(imgView);

                    progressBar.setVisibility(View.GONE);
                    profileImgView.setVisibility(View.VISIBLE);
                    profileEmailTextView.setVisibility(View.VISIBLE);
                    profileNameTextView.setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // for up arrow button
                Animatoo.animateZoom(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        Intent intent = new Intent(ProfilePage.this, LoginPage.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        startActivity(intent);
    }
    private void showConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("YES,LOGOUT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                logout();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
}