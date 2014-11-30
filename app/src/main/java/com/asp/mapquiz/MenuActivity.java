package com.asp.mapquiz;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

public class MenuActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        Drawable drawable = getResources().getDrawable(R.drawable.staticmap);
        final ImageView image = (ImageView) findViewById(R.id.menu_background);

        Animator animator = ObjectAnimator.ofFloat(new BackgroundImageView(image, drawable),
                "move", 0f, drawable.getIntrinsicWidth() - image.getWidth());
        animator.setDuration(20000);
        animator.start();
    }

    private class BackgroundImageView {
        private Matrix mMatrix;
        private ImageView mImageView;
        private Drawable mDrawable;

        public BackgroundImageView(ImageView view, Drawable draw) {
            mImageView = view;
            mDrawable = draw;
            mMatrix = new Matrix();
        }

        public void setMove(float dx) {
            mMatrix.reset();
            mMatrix.postTranslate(-dx, 0);
            mImageView.setImageMatrix(mMatrix);
        }
    }
}
