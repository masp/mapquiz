package com.asp.mapquiz.game;

public class ToTheDeathGame implements GameType {
    private int mScore;
    private boolean wasWrong;

    public ToTheDeathGame() {
        mScore = 0;
    }

    @Override
    public boolean shouldAskAnother(boolean wasCorrect) {
        if (wasCorrect) mScore++;
        else wasWrong = true;
        return wasCorrect;
    }

    @Override
    public int getQuestionsRight() {
        return mScore;
    }

    @Override
    public int getTotalQuestions() {
        int totalScore = mScore;
        if (wasWrong) totalScore++;
        return totalScore;
    }
}
