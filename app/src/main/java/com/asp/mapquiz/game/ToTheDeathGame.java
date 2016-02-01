package com.asp.mapquiz.game;

public class ToTheDeathGame implements GameType {
    private int mScore;
    private int mRight;
    private boolean wasWrong;

    public ToTheDeathGame() {
        mScore = 0;
    }

    @Override
    public boolean shouldAskAnother(int secondsRemaining, boolean wasCorrect) {
        if (wasCorrect) {
            mScore += secondsRemaining;
            mRight++;
        }
        else wasWrong = true;
        return wasCorrect;
    }

    @Override
    public ScorePair getScore() {
        return new ScorePair(mScore, mRight, wasWrong ? 1 : 0);
    }
}
