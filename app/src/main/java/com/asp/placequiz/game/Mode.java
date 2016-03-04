package com.asp.placequiz.game;

import com.asp.placequiz.question.QuestionFactory;

/**
 * Structure representing the mode properties possible (difficulty)
 */
public class Mode {
    public enum Difficulty {
        HARD(0, 12), MODERATE(1, 10), EASY(2, 9);

        private int zoomLevel;
        private int id;

        Difficulty(int id, int zoomLevel) {
            this.id = id;
            this.zoomLevel = zoomLevel;
        }

        public static Difficulty getDifficultyById(int id) {
            for (Difficulty diff : values()) {
                if (diff.getId() == id) {
                    return diff;
                }
            }
            return null;
        }

        public static String getReadableName(Difficulty difficulty) {
            return difficulty.name().substring(0, 1).toUpperCase() +
                    difficulty.name().substring(1).toLowerCase();
        }

        public int getZoomLevel() {
            return zoomLevel;
        }

        public int getId() {
            return id;
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

    /* Mode ID is uniquely expressed using the first 1 digit for game type, the next digit
     * for quiz type, and the next digit for difficulty
     */
    public int getModeId() {
        int modeId = mGameType.getId() * 100 + mQuizType.getId() * 10 + mDifficulty.getId() * 1;
        return modeId;
    }

    public Difficulty getDifficultyFromId(int modeId) {
        return Difficulty.getDifficultyById(splitId(modeId)[0]);
    }

    public QuestionFactory.QuizType getQuizTypeFromId(int modeId) {
        return QuestionFactory.QuizType.getQuizTypeByid(splitId(modeId)[1]);
    }

    public GameType.Name getGameTypeFromId(int modeId) {
        return GameType.Name.getGameTypeById(splitId(modeId)[2]);
    }

    // elements go as follows: difficulty, quiztype, gametype
    private int[] splitId(int modeId) {
        int[] data = new int[3];
        for (int i = 0; i < 3; i++) {
            data[i] = modeId % 10;
            modeId /= 10;
        }
        return data;
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
