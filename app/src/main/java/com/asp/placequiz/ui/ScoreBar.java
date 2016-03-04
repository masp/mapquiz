package com.asp.placequiz.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asp.placequiz.R;
import com.asp.placequiz.game.GameType;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.game.ScorePair;

public class ScoreBar extends FrameLayout {
    private int mTimeLimit;
    private int mRemainingSeconds;

    private TextView mGamemode;
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

        mGamemode = (TextView) findViewById(R.id.game_mode_type);
        mClock = (TextView) findViewById(R.id.score_board_clock);
        mCorrect = (TextView) findViewById(R.id.score_board_correct);
        mWrong = (TextView) findViewById(R.id.score_board_wrong);
        mRemainingSeconds = 0;
    }

    public void setTimeLimit(int seconds) {
        mTimeLimit = seconds;
        mRemainingSeconds = mTimeLimit;
    }

    public void setGameType(GameType.Name name) {
        mGamemode.setText(GameType.Name.getReadableName(name));
    }

    public void setScore(ScorePair score) {
        mCorrect.setText(String.valueOf(score.getNumberCorrectAnswers()));
        mWrong.setText(String.valueOf(score.getNumberWrongAnswers()));
    }

    public void resetTimer() {
        mRemainingSeconds = mTimeLimit;
        updateClockUI();
    }

    public void tickDown() {
        mRemainingSeconds--;
        updateClockUI();
    }

    public int getRemainingTime() {
        return mRemainingSeconds;
    }

    private void updateClockUI() {
        mClock.setText(String.valueOf(mRemainingSeconds));
    }
}