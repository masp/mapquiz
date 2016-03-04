package com.asp.placequiz.ui;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {
    private static final long ONE_SECOND = 1000L;

    public interface OnTimerEndListener {
        void onTimeEnd();
    }

    public interface TimerListener {
        void onTick();
    }

    private Handler mContext;
    private Timer mTimer;

    private int mDuration;
    private long mTimeRemaining;

    private List<TimerListener> mTimerListeners;
    private OnTimerEndListener mTimerEndListener;

    private boolean mRunning;
    private boolean mPaused;

    public GameTimer(Handler uiScheduler, int duration) {
        mContext = uiScheduler;
        mDuration = duration;
        mTimerListeners = new ArrayList<>();
    }

    public void addTimerListener(TimerListener listener) {
        mTimerListeners.add(listener);
    }

    public void setEndTimerListener(OnTimerEndListener listener) {
        mTimerEndListener = listener;
    }

    public void start() {
        mTimer = new Timer("GameTimer");
        mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mContext.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPaused()) {
                                mTimeRemaining -= 50L;

                                if (mTimeRemaining % 1000 == 0) {
                                    for (TimerListener listener : mTimerListeners) {
                                        listener.onTick();
                                    }
                                }

                                if (mTimeRemaining == 0) {
                                    mTimerEndListener.onTimeEnd();
                                    mTimeRemaining = -1;
                                }
                            }
                        }
                    });
                }
            }, 0L, 50L);
        mRunning = true;
        mTimeRemaining = mDuration * 1000L;
    }

    public long getCurrentTime() {
        return mTimeRemaining;
    }

    public int getMaxTime() {
        return mDuration;
    }

    public void pause() {
        mPaused = true;
    }

    public boolean isPaused() {
        return mPaused;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void resume() {
        mPaused = false;
    }

    public void stop() {
        mRunning = false;
        mTimer.cancel();
    }
}
