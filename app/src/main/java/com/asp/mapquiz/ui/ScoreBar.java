package com.asp.mapquiz.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asp.mapquiz.R;

import java.util.concurrent.TimeUnit;

public class ScoreBar extends FrameLayout {
    private long mMilliseconds;

    private TextView mClock;
    private TextView mCorrect;
    private TextView mWrong;

    public ScoreBar(Context context) {
        super(context);
        init(context);
    }

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScoreBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.score_board, null);
        addView(layout);

        mClock = (TextView) findViewById(R.id.score_board_clock);
        mCorrect = (TextView) findViewById(R.id.score_board_correct);
        mWrong = (TextView) findViewById(R.id.score_board_wrong);
    }

    public void setCorrect(int numCorrect) {
        mCorrect.setText(Integer.toString(numCorrect));
    }

    public void setIncorrect(int numWrong) {
        mWrong.setText(Integer.toString(numWrong));
    }

    public void updateTime(long millis) {
        mMilliseconds += millis;
        String formattedTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(mMilliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMilliseconds)));
        mClock.setText(formattedTime);
    }
}
