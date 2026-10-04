package com.example.keepnotes.ui.drawing;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.keepnotes.R;
import com.kyanogen.signatureview.SignatureView;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateDrawing extends AppCompatActivity {

    private SignatureView signatureView;
    private ImageButton eraserButton;
    private TextView penSizeLabel;
    private TextView sizeModeLabel;

    private DrawingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drawing);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DrawingViewModel.class);

        // Bind UI references
        signatureView = findViewById(R.id.signature_view);
        penSizeLabel = findViewById(R.id.txt_pen_size);
        sizeModeLabel = findViewById(R.id.txt_size_mode);
        eraserButton = findViewById(R.id.btn_eraser);

        // Apply initial state from ViewModel
        applyDrawingState();

        // Observe UI state changes for reactive updates
        viewModel.getUiState().observe(this, new Observer<DrawingViewModel.DrawingUiState>() {
            @Override
            public void onChanged(DrawingViewModel.DrawingUiState state) {
                if (state != null) {
                    // Update UI elements based on state changes
                    float activeSize = state.isEraserMode() ? state.getEraserSize() : state.getPenSize();
                    penSizeLabel.setText((int) activeSize + " dp");
                    sizeModeLabel.setText(state.getSizeModeLabel());
                    signatureView.setEraserMode(state.isEraserMode());
                    signatureView.setEraserSize(state.getEraserSize());
                    signatureView.setPenSize(state.getPenSize());
                    SeekBar sizeSeekBar = findViewById(R.id.seek_pen_size);
                    sizeSeekBar.setProgress((int) activeSize - 1);
                    eraserButton.setBackgroundResource(state.isEraserMode()
                        ? R.drawable.drawing_tool_selected
                        : R.drawable.drawing_eraser_button);
                }
            }
        });

        // Toolbar actions
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_undo).setOnClickListener(v -> signatureView.undo());
        findViewById(R.id.btn_redo).setOnClickListener(v -> signatureView.redo());
        findViewById(R.id.btn_clear).setOnClickListener(v -> signatureView.clearCanvas());

        // Pen size seekbar configuration
        SeekBar penSizeSeekBar = findViewById(R.id.seek_pen_size);
        penSizeSeekBar.setMax(59); // 1-60 dp range
        penSizeSeekBar.setProgress((int) viewModel.getPenSize() - 1);

        penSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress + 1;
                viewModel.setPenSize(size);

                // Apply to SignatureView
                signatureView.setPenSize(size);

                // Update label
                penSizeLabel.setText(size + " dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed
            }
        });

        // Eraser toggle button
        eraserButton.setOnClickListener(v -> {
            boolean currentlyEraser = viewModel.isEraserMode();
            viewModel.setEraserMode(!currentlyEraser);
        });

        // Color selection buttons
        bindColorButton(R.id.color_black, R.color.drawing_ink);
        bindColorButton(R.id.color_blue, R.color.drawing_blue);
        bindColorButton(R.id.color_red, R.color.drawing_red);
        bindColorButton(R.id.color_green, R.color.drawing_green);

        // Custom color picker
        findViewById(R.id.btn_color_picker).setOnClickListener(v -> openColorPicker());
    }

    /**
     * Applies the current drawing state to the SignatureView.
     */
    private void applyDrawingState() {
        DrawingViewModel.DrawingUiState state = viewModel.getCurrentState();
        signatureView.setPenColor(state.getSelectedColor());
        signatureView.setPenSize(state.getPenSize());
        signatureView.setEraserSize(state.getEraserSize());
        signatureView.setEraserMode(state.isEraserMode());

        float activeSize = state.isEraserMode() ? state.getEraserSize() : state.getPenSize();
        penSizeLabel.setText((int) activeSize + " dp");
        sizeModeLabel.setText(state.getSizeModeLabel());

        eraserButton.setBackgroundResource(state.isEraserMode()
            ? R.drawable.drawing_tool_selected
            : R.drawable.drawing_eraser_button);
    }

    /**
     * Binds a color selection button to its color value.
     */
    private void bindColorButton(int viewId, int colorResId) {
        findViewById(viewId).setOnClickListener(v -> {
            int color = ContextCompat.getColor(this, colorResId);
            viewModel.setPenColor(color);
            viewModel.setEraserMode(false);

            // Apply to SignatureView immediately for visual feedback
            signatureView.setPenColor(color);

            // Update eraser button background to non-selected state
            eraserButton.setBackgroundResource(R.drawable.drawing_eraser_button);
        });
    }

    /**
     * Opens the Android color picker dialog for custom color selection.
     */
    private void openColorPicker() {
        new AmbilWarnaDialog(this, viewModel.getSelectedColor(),
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // No action needed on cancel
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        viewModel.setPenColor(color);
                        viewModel.setEraserMode(false);
                        signatureView.setPenColor(color);

                        // Reset eraser button state
                        eraserButton.setBackgroundResource(R.drawable.drawing_eraser_button);
                    }
                }).show();
    }
}
