package com.asp.placequiz.game;

public interface GameType {
    enum Name {
        CHALLENGE(0, ToTheDeathGame.class), BEST_OF_10(1, BestOfTenGame.class),
            UNLIMITED(2, UnlimitedGame.class);

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
            StringBuilder builder = new StringBuilder();
            String[] words = gameType.name().split("_");
            builder.append(capitalize(words[0]));
            for (int i = 1; i < words.length; i++) {
                builder.append(" " + capitalize(words[i]));
            }
            return builder.toString();
        }

        private static String capitalize(String word) {
            return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
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
