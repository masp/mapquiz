package com.asp.placequiz.game;

public class UnlimitedGame implements GameType {
    private int mScore = 0;
    private int mRight = 0;
    private int mWrong = 0;

    @Override
    public boolean shouldAskAnother(int secondsRemaining, boolean wasCorrect) {
        if (wasCorrect) {
            mScore += secondsRemaining;
            mRight++;
        } else {
            mWrong++;
        }

        return true;
    }

    @Override
    public ScorePair getScore() {
        return new ScorePair(mScore, mRight, mWrong);
    }
}
