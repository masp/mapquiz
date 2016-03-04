package com.asp.placequiz.game;

import android.util.Log;

public class GameTypeFactory {
    private static final String TAG = "GameTypeFactory";

    public static GameType createGameType(GameType.Name gameType) {
        try {
            return gameType.getGameType().newInstance();
        } catch (Exception ex) {
            Log.e(TAG, "Error instantiating game type " + gameType.name());
            ex.printStackTrace();
        }
        return new BestOfTenGame();
    }
}
