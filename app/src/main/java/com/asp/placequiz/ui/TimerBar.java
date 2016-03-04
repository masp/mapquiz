package com.asp.placequiz.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.asp.placequiz.activities.GameActivity;

public class TimerBar extends RoundCornerProgressBar {
    private ValueAnimator mBarAnimator;
    private long mElapsedTime;

    public TimerBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBarAnimator = ValueAnimator.ofFloat(1f, 0f);
        mBarAnimator.setDuration(GameActivity.TIME_LIMIT * 1000L);
        mBarAnimator.setInterpolator(new LinearInterpolator());
        mBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setProgress(getMax() * (float) valueAnimator.getAnimatedValue());
            }
        });
    }

    public void reset() {
        setProgress(getMax());
    }

    public void pause() {
        mBarAnimator.cancel();
    }

    public void resume(GameTimer timer) {
        mBarAnimator.start();
        mBarAnimator.setCurrentPlayTime(GameActivity.TIME_LIMIT * 1000L - timer.getCurrentTime());
    }

    public void start() {
        mBarAnimator.start();
    }

    public void stop() {
        mBarAnimator.cancel();
    }
}
