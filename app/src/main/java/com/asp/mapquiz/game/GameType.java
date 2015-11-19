package com.asp.mapquiz.game;

public interface GameType {
    enum Name {
        TO_THE_DEATH(ToTheDeathGame.class), BEST_OF_10(BestOfTenGame.class);

        private Class<? extends GameType> gameType;

        Name(Class<? extends GameType> gameType) {
            this.gameType = gameType;
        }

        public Class<? extends GameType> getGameType() {
            return gameType;
        }
    }

    boolean shouldAskAnother(boolean wasCorrect);
    int getQuestionsRight();
    int getTotalQuestions();
}
