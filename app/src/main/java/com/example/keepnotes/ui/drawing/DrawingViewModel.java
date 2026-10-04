package com.example.keepnotes.ui.drawing;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.keepnotes.data.repository.DrawingRepository;
import com.example.keepnotes.R;

/**
 * ViewModel for the drawing feature. Handles all drawing-related business logic,
 * including pen color, size, eraser mode, and canvas state. Exposes UI state as LiveData
 * so the UI can react to changes in a lifecycle-aware manner.
 */
public class DrawingViewModel extends ViewModel {

    // Constants for drawing tools - define colors in one place for consistency
    public static final int COLOR_BLACK = Color.parseColor("#202534");
    public static final int COLOR_BLUE = Color.parseColor("#536DFE");
    public static final int COLOR_RED = Color.parseColor("#EF5350");
    public static final int COLOR_GREEN = Color.parseColor("#26A69A");
    public static final int DEFAULT_PEN_SIZE = 5;
    public static final int DEFAULT_ERASER_SIZE = 20;

    // Drawing state exposed as LiveData for lifecycle-aware observation
    private final MutableLiveData<DrawingUiState> uiState = new MutableLiveData<>(new DrawingUiState());
    private final DrawingUiState currentState;

    private DrawingRepository repository;
    private Context context;

    public DrawingViewModel() {
        this.currentState = uiState.getValue();
    }

    public DrawingViewModel(DrawingRepository repository) {
        this.repository = repository;
        this.currentState = uiState.getValue();
    }

    public DrawingViewModel(SavedStateHandle savedStateHandle) {
        this.currentState = uiState.getValue();
    }

    public DrawingViewModel(Context context, DrawingRepository repository) {
        this.context = context;
        this.repository = repository;
        this.currentState = uiState.getValue();
    }

    // ==================== Drawing State Management ====================

    /**
     * Sets the current pen color. This is the main drawing color used when eraser is off.
     */
    public void setPenColor(int color) {
        currentState.setSelectedColor(color);
        notifyUiStateChanged();
    }

    /**
     * Sets the pen size. When eraser mode is active, this sets the eraser size instead.
     */
    public void setPenSize(float size) {
        if (currentState.isEraserMode) {
            currentState.eraserSize = size;
        } else {
            currentState.penSize = size;
        }
        notifyUiStateChanged();
    }

    /**
     * Toggles eraser mode on/off. Eraser mode overrides the pen color with white
     * and uses the eraser size for strokes.
     */
    public void setEraserMode(boolean enabled) {
        currentState.isEraserMode = enabled;
        notifyUiStateChanged();
    }

    // ==================== Public API for Drawing State ====================

    public DrawingUiState getCurrentState() {
        return currentState;
    }

    public LiveData<DrawingUiState> getUiState() {
        return uiState;
    }

    public int getSelectedColor() {
        return currentState.selectedColor;
    }

    public float getPenSize() {
        return currentState.penSize;
    }

    public boolean isEraserMode() {
        return currentState.isEraserMode;
    }

    public int getPenSizeDp() {
        return (int) currentState.penSize;
    }

    public int getEraserSizeDp() {
        return (int) currentState.eraserSize;
    }

    public String getSizeModeLabel() {
        return currentState.isEraserMode ? "Eraser size" : "Pencil size";
    }

    // ==================== Repository Access ====================

    public DrawingRepository getRepository() {
        return repository;
    }

    /**
     * Helper to convert dp to pixels using the provided context.
     */
    public int dpToPx(int dp) {
        if (context == null) {
            return dp;
        }
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Helper to get color from resources.
     */
    public int getColorResColor(int resId) {
        if (context == null) {
            return Color.BLACK;
        }
        return context.getResources().getColor(resId);
    }

    // ==================== Factory Pattern ====================

    /**
     * Factory class for creating DrawingViewModel instances with custom dependencies.
     * Use this to inject your own Repository, or use the default no-arg constructor.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final DrawingRepository repository;

        public Factory(DrawingRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DrawingViewModel.class)) {
                return (T) new DrawingViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }

    // ==================== Canvas Operations ====================

    /**
     * Saves the current canvas state to the drawing repository for potential persistence.
     */
    public void saveCanvasState() {
        if (repository != null) {
            // Future: implement canvas persistence logic here
        }
    }

    // ==================== Internal Methods ====================

    private void notifyUiStateChanged() {
        uiState.setValue(currentState);
    }

    /**
     * Holds the complete state of the drawing UI. This data class is exposed as LiveData
     * to allow the UI to observe and react to state changes.
     */
    public static class DrawingUiState {
        private int selectedColor = COLOR_BLACK;
        private float penSize = DEFAULT_PEN_SIZE;
        private float eraserSize = DEFAULT_ERASER_SIZE;
        private boolean isEraserMode = false;

        // Getters
        public int getSelectedColor() {
            return selectedColor;
        }

        public float getPenSize() {
            return penSize;
        }

        public float getEraserSize() {
            return eraserSize;
        }

        public boolean isEraserMode() {
            return isEraserMode;
        }

        public String getSizeModeLabel() {
            return isEraserMode ? "Eraser size" : "Pencil size";
        }

        // Setters
        public void setSelectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
        }

        public void setPenColor(int penColor) {
            this.selectedColor = penColor;
        }

        public void setPenSize(float penSize) {
            this.penSize = penSize;
        }

        public void setEraserSize(float eraserSize) {
            this.eraserSize = eraserSize;
        }

        public void setEraserMode(boolean eraserMode) {
            this.isEraserMode = eraserMode;
        }
    }
}