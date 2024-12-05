package com.example.myocrtest;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private float[] matrixValues = new float[9];
    private float scale = 1f;
    private float minScale = 0.5f;
    private float maxScale = 5f;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private float lastX, lastY;
    private boolean isDragging;

    private int previousPointerCount = 0;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());

        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        int currentPointerCount = event.getPointerCount();
        if (currentPointerCount == 1 && previousPointerCount != 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    isDragging = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isDragging) {
                        float dx = event.getX() - lastX;
                        float dy = event.getY() - lastY;

                        matrix.postTranslate(dx, dy);
                        setImageMatrix(matrix);

                        lastX = event.getX();
                        lastY = event.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isDragging = false;
                    break;
            }
        }
        previousPointerCount = currentPointerCount;
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float currentScale = getCurrentScale();

            if ((currentScale * scaleFactor >= minScale) && (currentScale * scaleFactor <= maxScale)) {
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                setImageMatrix(matrix);
            }
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (getCurrentScale() > minScale) {
                matrix.setScale(minScale, minScale);
            } else {
                matrix.postScale(2f, 2f, e.getX(), e.getY());
            }
            setImageMatrix(matrix);
            return true;
        }
    }

    private float getCurrentScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }
}
