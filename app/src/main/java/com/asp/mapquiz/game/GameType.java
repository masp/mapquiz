package com.asp.mapquiz.game;

public interface GameType {
    enum Name {
        TO_THE_DEATH(0, ToTheDeathGame.class), BEST_OF_10(1, BestOfTenGame.class);

        private Class<? extends GameType> gameType;
        private int id;

        Name(int id, Class<? extends GameType> gameType) {
            this.gameType = gameType;
            this.id = id;
        }

        public static Name getGameTypeById(int id) {
            for (Name name : values()) {
                if (name.getId() == id) {
                    return name;
                }
            }
            return null;
        }

        public static String getReadableName(Name gameType) {
            String readableName = gameType.name();
            for (String word : readableName.split("\\s")) {
                readableName += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }
            return readableName;
        }

        public Class<? extends GameType> getGameType() {
            return gameType;
        }

        public int getId() {
            return id;
        }
    }

    boolean shouldAskAnother(int secondsRemaining, boolean wasCorrect);

    ScorePair getScore();
}
