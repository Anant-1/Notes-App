package com.kyanogen.signatureview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

import com.example.keepnotes.R;

public class SignatureView extends View {

    private Path path = new Path();
    private Paint paint = new Paint();
    private Paint canvasPaint = new Paint(Paint.DITHER_FLAG);
    private final Paint eraserCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint eraserCursorFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas bitmapCanvas;
    private Bitmap signatureBitmap;

    private int penColor = Color.BLACK;
    private float penSize = 5f;
    private float eraserSize;
    private int backgroundColor = Color.WHITE;
    private boolean enableSignature = true;
    private boolean isBitmapEmpty = true;
    private boolean eraserMode;
    private boolean showEraserCursor;
    private float eraserCursorX;
    private float eraserCursorY;

    private float lastX, lastY;
    private final Deque<CanvasState> undoStack = new ArrayDeque<>();
    private final Deque<CanvasState> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORY = 10;

    private static class CanvasState {
        final Bitmap bitmap;
        final boolean empty;

        CanvasState(Bitmap bitmap, boolean empty) {
            this.bitmap = bitmap;
            this.empty = empty;
        }
    }

    public SignatureView(Context context) {
        super(context);
        init(context, null);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        penSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        eraserSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SignatureView);
            try {
                penColor = a.getColor(R.styleable.SignatureView_penColor, Color.BLACK);
                penSize = a.getDimension(R.styleable.SignatureView_penSize, penSize);
                backgroundColor = a.getColor(R.styleable.SignatureView_backgroundColor, Color.WHITE);
                enableSignature = a.getBoolean(R.styleable.SignatureView_enableSignature, true);
            } finally {
                a.recycle();
            }
        }

        paint.setAntiAlias(true);
        paint.setColor(penColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(penSize);
        eraserCursorPaint.setColor(Color.rgb(83, 109, 254));
        eraserCursorPaint.setStyle(Paint.Style.STROKE);
        eraserCursorPaint.setStrokeWidth(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        eraserCursorFillPaint.setColor(Color.argb(24, 83, 109, 254));
        eraserCursorFillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            signatureBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(signatureBitmap);
            bitmapCanvas.drawColor(backgroundColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (signatureBitmap != null) {
            canvas.drawBitmap(signatureBitmap, 0, 0, canvasPaint);
        }
        canvas.drawPath(path, paint);
        if (eraserMode && showEraserCursor) {
            float radius = Math.max(penSize / 2f, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            canvas.drawCircle(eraserCursorX, eraserCursorY, radius, eraserCursorFillPaint);
            canvas.drawCircle(eraserCursorX, eraserCursorY, radius, eraserCursorPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enableSignature) {
            return false;
        }

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                saveToUndoStack();
                redoStack.clear();
                path.reset();
                path.moveTo(eventX, eventY);
                lastX = eventX;
                lastY = eventY;
                eraserCursorX = eventX;
                eraserCursorY = eventY;
                showEraserCursor = eraserMode;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(eventX - lastX);
                float dy = Math.abs(eventY - lastY);
                eraserCursorX = eventX;
                eraserCursorY = eventY;
                if (dx >= 4 || dy >= 4) {
                    path.quadTo(lastX, lastY, (eventX + lastX) / 2, (eventY + lastY) / 2);
                    lastX = eventX;
                    lastY = eventY;
                    isBitmapEmpty = false;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                path.lineTo(lastX, lastY);
                if (bitmapCanvas != null) {
                    bitmapCanvas.drawPath(path, paint);
                }
                path.reset();
                showEraserCursor = false;
                invalidate();
                return true;

            default:
                return false;
        }
    }

    public void setPenSize(float size) {
        this.penSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
        updatePaintForTool();
    }

    public void setEraserSize(float size) {
        this.eraserSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
        updatePaintForTool();
    }

    public void setPenColor(int color) {
        this.penColor = color;
        updatePaintForTool();
    }

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
        updatePaintForTool();
        if (!eraserMode) showEraserCursor = false;
        invalidate();
    }

    private void updatePaintForTool() {
        paint.setColor(eraserMode ? backgroundColor : penColor);
        paint.setStrokeWidth(eraserMode ? eraserSize : penSize);
    }

    public void clearCanvas() {
        if (bitmapCanvas == null) return;
        saveToUndoStack();
        redoStack.clear();
        path.reset();
        bitmapCanvas.drawColor(backgroundColor);
        isBitmapEmpty = true;
        invalidate();
    }

    public void undo() {
        if (undoStack.isEmpty() || bitmapCanvas == null) return;
        redoStack.push(captureState());
        restoreState(undoStack.pop());
    }

    public void redo() {
        if (redoStack.isEmpty() || bitmapCanvas == null) return;
        undoStack.push(captureState());
        restoreState(redoStack.pop());
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    private CanvasState captureState() {
        return new CanvasState(signatureBitmap.copy(Bitmap.Config.ARGB_8888, false), isBitmapEmpty);
    }

    private void saveToUndoStack() {
        if (signatureBitmap == null) return;
        undoStack.push(captureState());
        while (undoStack.size() > MAX_HISTORY) undoStack.removeLast();
    }

    private void restoreState(CanvasState state) {
        bitmapCanvas.drawColor(backgroundColor);
        bitmapCanvas.drawBitmap(state.bitmap, 0, 0, canvasPaint);
        isBitmapEmpty = state.empty;
        path.reset();
        invalidate();
    }

    public Bitmap getSignatureBitmap() {
        return signatureBitmap;
    }

    public boolean isBitmapEmpty() {
        return isBitmapEmpty;
    }

    public void setEnableSignature(boolean enableSignature) {
        this.enableSignature = enableSignature;
    }

    public float getPenSize() {
        return penSize;
    }

    public int getPenColor() {
        return penColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
