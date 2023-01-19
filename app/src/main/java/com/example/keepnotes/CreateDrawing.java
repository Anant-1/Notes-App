package com.example.keepnotes;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;

import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kyanogen.signatureview.SignatureView;

import java.security.Signature;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateDrawing extends AppCompatActivity {
    private SignatureView signatureView;
    private ImageButton imgEraser, imgColor;
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
        scrollView = findViewById(R.id.scrollView);

//        signatureView.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                System.out.println("In the method");
//                int action = motionEvent.getAction();
//                switch (action){
//                    case MotionEvent.ACTION_DOWN:
//                        // Disable the scroll view to intercept the touch event
//                        scrollView.requestDisallowInterceptTouchEvent(true);
//                        return false;
//                    case MotionEvent.ACTION_UP:
//                        // Allow scroll View to intercept the touch event
//                        scrollView.requestDisallowInterceptTouchEvent(false);
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        scrollView.requestDisallowInterceptTouchEvent(true);
//                        return false;
//                    default:
//                        return true;
//                }
//            }
//        });

        defaultColor = ContextCompat.getColor(this, R.color.black);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textPenSize.setText(progress+"dp");
                signatureView.setPenSize(progress);
                seekBar.setMax(50);
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
                openColorPicker();
            }
        });
        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
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
                signatureView.setPenColor(color);

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
}