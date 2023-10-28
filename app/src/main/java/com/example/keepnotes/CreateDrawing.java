package com.example.keepnotes;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.security.Signature;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateDrawing extends AppCompatActivity {
    private SignatureView signatureView;
    private ImageButton imgEraser, imgColor, imgPen;
    private SeekBar seekBar;
    private TextView textPenSize;
    private int defaultColor;
    private ScrollView scrollView;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drawing);
        signatureView = findViewById(R.id.signature_view);
        seekBar = findViewById(R.id.seek_pen_size);
        textPenSize = findViewById(R.id.txt_pen_size);
        imgEraser = findViewById(R.id.btn_eraser);
        imgColor = findViewById(R.id.btn_color_picker);
        imgPen = findViewById(R.id.btn_pen);
        scrollView = findViewById(R.id.scroll_view_drawing);
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        signatureView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // Disable the scroll view to intercept the touch event
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow scroll View to intercept the touch event
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            }
        });
        defaultColor = ContextCompat.getColor(this, R.color.black);
        signatureView.setPenColor(defaultColor);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textPenSize.setText(Integer.toString(progress));
                signatureView.setPenSize(progress);
                seekBar.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try{
//                    Bitmap bitmap = signatureView.getDrawingCache();
//                    Intent intent = new Intent(CreateDrawing.this, AddNoteActivity.class);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    intent.putExtra("drawing", byteArray);
//                    startActivity(intent);
//                }catch (Exception e) {
//                    System.out.println("drawing- " + e.getMessage());
//                }
                openColorPicker();
            }
        });
        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.setPenColor(ContextCompat.getColor(CreateDrawing.this, R.color.white));
            }
        });
        imgEraser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showConfirmationDialog();
                return false;
            }
        });
        imgPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                signatureView.setPenColor(defaultColor);
                imgPen.setColorFilter(defaultColor);
            }
        });
        ambilWarnaDialog.show();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollView.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                scrollView.requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
  }
    private void showConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm the action");
        builder.setMessage("Are you sure you want clear the canvas?");
        builder.setPositiveButton("CLEAR ALL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                signatureView.clearCanvas();
                signatureView.setPenColor(defaultColor);
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.yellow_color_of_fab));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.yellow_color_of_fab));
    }
}