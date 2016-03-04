package com.asp.placequiz.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class MapBackgroundView extends ImageView {
    private static final String BACKGROUND_VIEW = "BACKGROUND_VIEW";

    private static final long FADE_DURATION = 1000L;
    private static final long MOVE_DURATION = 10000L;
    private static final int MOVE_DISTANCE_X = 300; // in pixels
    private static final int MOVE_DISTANCE_Y = 200;
    private static final float MINIMUM_ALPHA = 0.4f;

    private Random random;

    private void init(Context context) {
        random = new Random();

        MapBackground.BitmapManager bpManager = MapBackground.BitmapManager.getInstance();
        setImageBitmap(bpManager.getMapBitmap(context));
    }

    public MapBackgroundView(Context context) {
        super(context);
        init(context);
    }

    public MapBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapBackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        startNewMovement(width, height);
    }

    public void startNewMovement(int width, int height) {
        Point start = getRandomPointInBounds(getDrawable(), width, height);
        Point end = new Point();
        end.x = start.x + (MOVE_DISTANCE_X * getRandomSign());
        end.y = start.y + (MOVE_DISTANCE_Y * getRandomSign());

        MapBackground background = new MapBackground(this);
        Animator transXAnimator = ObjectAnimator
                .ofFloat(background, "translationX", start.x, end.x);
        transXAnimator.setDuration(MOVE_DURATION);
        transXAnimator.setInterpolator(new DecelerateInterpolator());
        Animator transYAnimator = ObjectAnimator
                .ofFloat(background, "translationY", start.y, end.y);
        transYAnimator.setDuration(MOVE_DURATION);
        transYAnimator.setInterpolator(new DecelerateInterpolator());
        Animator alphaAnimator = ObjectAnimator
                .ofFloat(this, ImageView.ALPHA, MINIMUM_ALPHA, 1f);
        alphaAnimator.setDuration(FADE_DURATION);

        AnimatorSet animator = new AnimatorSet();
        animator.play(transXAnimator)
                .with(transYAnimator)
                .with(alphaAnimator);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Animator fadeOut = ObjectAnimator
                        .ofFloat(MapBackgroundView.this, ImageView.ALPHA, 1f, MINIMUM_ALPHA);
                fadeOut.setDuration(FADE_DURATION);
                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startNewMovement(MapBackgroundView.this.getWidth(),
                                MapBackgroundView.this.getHeight());
                    }
                });
                fadeOut.start();
            }
        });

        animator.start();
    }

    private int getRandomSign() {
        int randomSign = (int) Math.signum(random.nextInt());
        if (randomSign == 0) randomSign = 1;
        return randomSign;
    }

    private Point getRandomPointInBounds(Drawable drawable, int width, int height) {
        int possiblePhysicalWidth = drawable.getIntrinsicWidth() - width;
        int possiblePhysicalHeight = drawable.getIntrinsicHeight() - height;

        int possibleBoundWidth = possiblePhysicalWidth - (MOVE_DISTANCE_X * 2);
        int possibleBoundHeight = possiblePhysicalHeight - (MOVE_DISTANCE_Y * 2);

        Point point = new Point();
        point.x = random.nextInt(possibleBoundWidth) + MOVE_DISTANCE_X;
        point.y = random.nextInt(possibleBoundHeight) + MOVE_DISTANCE_Y;
        return point;
    }
}
