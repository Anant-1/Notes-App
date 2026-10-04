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

import com.example.keepnotes.R;

public class SignatureView extends View {

    private Path path = new Path();
    private Paint paint = new Paint();
    private Paint canvasPaint = new Paint(Paint.DITHER_FLAG);
    private Canvas bitmapCanvas;
    private Bitmap signatureBitmap;

    private int penColor = Color.BLACK;
    private float penSize = 5f;
    private int backgroundColor = Color.WHITE;
    private boolean enableSignature = true;
    private boolean isBitmapEmpty = true;

    private float lastX, lastY;

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
                path.reset();
                path.moveTo(eventX, eventY);
                lastX = eventX;
                lastY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(eventX - lastX);
                float dy = Math.abs(eventY - lastY);
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
                invalidate();
                return true;

            default:
                return false;
        }
    }

    public void setPenSize(float size) {
        this.penSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
        paint.setStrokeWidth(this.penSize);
    }

    public void setPenColor(int color) {
        this.penColor = color;
        paint.setColor(this.penColor);
    }

    public void clearCanvas() {
        path.reset();
        if (bitmapCanvas != null) {
            bitmapCanvas.drawColor(backgroundColor);
        }
        isBitmapEmpty = true;
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
