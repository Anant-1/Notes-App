package com.example.keepnotes.data.repository;

import android.content.Context;
import android.graphics.Color;

/**
 * Repository to manage drawing configurations, tool preferences, and persistence abstractions.
 */
public class DrawingRepository {

    private static final int DEFAULT_COLOR = Color.parseColor("#202534");
    private static final float DEFAULT_PEN_SIZE = 5f;
    private static final float DEFAULT_ERASER_SIZE = 20f;

    private int selectedColor = DEFAULT_COLOR;
    private float penSize = DEFAULT_PEN_SIZE;
    private float eraserSize = DEFAULT_ERASER_SIZE;
    private boolean isEraserMode = false;

    public DrawingRepository() {
    }

    public DrawingRepository(Context context) {
        // Can be extended with SharedPreferences for persistent drawing tool preferences
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }

    public float getPenSize() {
        return penSize;
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
    }

    public float getEraserSize() {
        return eraserSize;
    }

    public void setEraserSize(float eraserSize) {
        this.eraserSize = eraserSize;
    }

    public boolean isEraserMode() {
        return isEraserMode;
    }

    public void setEraserMode(boolean eraserMode) {
        this.isEraserMode = eraserMode;
    }
}