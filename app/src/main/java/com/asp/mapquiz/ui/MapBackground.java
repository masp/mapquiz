package com.asp.mapquiz.ui;

import android.graphics.Matrix;
import android.widget.ImageView;

public class MapBackground {
    private ImageView image;
    private float x;
    private float y;

    public MapBackground(ImageView image) {
        this.image = image;
    }

    // Called by ObjectAnimator
    public void setTranslationX(float x) {
        this.x = x;
        calculateMatrix();
    }

    // Called by ObjectAnimator
    public void setTranslationY(float y) {
        this.y = y;
        calculateMatrix();
    }

    private void calculateMatrix() {
        Matrix matrix = new Matrix();
        matrix.setTranslate(-x, -y);
        image.setImageMatrix(matrix);
    }
}