package com.asp.mapquiz.game;

import com.asp.mapquiz.question.QuestionFactory;

/**
 * Structure representing the mode properties possible (difficulty)
 */
public class Mode {
    public enum Difficulty {
        HARD(11), MODERATE(9), EASY(7);

        private int mZoomLevel;

        Difficulty(int zoomLevel) {
            mZoomLevel = zoomLevel;
        }

        public int getZoomLevel() {
            return mZoomLevel;
        }
    }

    private GameType.Name mGameType;
    private QuestionFactory.QuizType mQuizType;
    private Difficulty mDifficulty;

    private Mode(GameType.Name gameType, QuestionFactory.QuizType quizType, Difficulty difficulty) {
        this.mGameType = gameType;
        this.mQuizType = quizType;
        this.mDifficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return mDifficulty;
    }

    public QuestionFactory.QuizType getQuizType() {
        return mQuizType;
    }

    public GameType.Name getGameType() {
        return mGameType;
    }

    public static class ModeBuilder {
        private GameType.Name gameType;
        private QuestionFactory.QuizType quizType;
        private Difficulty difficulty;

        public ModeBuilder withDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public ModeBuilder withQuizType(QuestionFactory.QuizType type) {
            this.quizType = type;
            return this;
        }

        public ModeBuilder withGameType(GameType.Name gameType) {
            this.gameType = gameType;
            return this;
        }

        public Mode build() {
            if (gameType == null || difficulty == null || quizType == null)
                throw new IllegalStateException("Must set quiz type, difficulty and game type to " +
                        "build!");
            return new Mode(gameType, quizType, difficulty);
        }
    }
}
