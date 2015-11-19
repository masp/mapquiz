package com.asp.mapquiz.game;

public class BestOfTenGame implements GameType {
    private final static int MAX_QUESTIONS = 10;
    private int mTotalCounter = 0;
    private int mScore = 0;

    @Override
    public boolean shouldAskAnother(boolean wasCorrect) {
        if (wasCorrect) mScore++;
        mTotalCounter++;
        return mTotalCounter < MAX_QUESTIONS;
    }

    @Override
    public int getQuestionsRight() {
        return mScore;
    }

    @Override
    public int getTotalQuestions() {
        return mTotalCounter;
    }
}
