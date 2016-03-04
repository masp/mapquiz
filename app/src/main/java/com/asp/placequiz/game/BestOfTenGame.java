package com.asp.placequiz.game;

public class BestOfTenGame implements GameType {
    private final static int MAX_QUESTIONS = 10;
    private int mTotalCounter = 0;
    private int mRight = 0;
    private int mScore;

    @Override
    public boolean shouldAskAnother(int secondsRemaining, boolean wasCorrect) {
        if (wasCorrect) {
            mRight++;
            mScore += secondsRemaining;
        }
        mTotalCounter++;
        return mTotalCounter < MAX_QUESTIONS;
    }

    @Override
    public ScorePair getScore() {
        return new ScorePair(mScore, mRight, mTotalCounter - mRight);
    }
}
