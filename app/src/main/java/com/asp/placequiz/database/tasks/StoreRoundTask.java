package com.asp.placequiz.database.tasks;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.asp.placequiz.activities.GameActivity;
import com.asp.placequiz.database.MapQuizContract;
import com.asp.placequiz.database.MapQuizDbHelper;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.game.ScorePair;

public class StoreRoundTask extends AsyncTask<ScorePair, Void, Void> {
    private Application mApp;
    private Mode mMode;

    public StoreRoundTask(Application app, Mode mode) {
        mApp = app;
        mMode = mode;
    }

    @Override
    protected Void doInBackground(ScorePair... scores) {
        if (scores.length != 1) throw new IllegalArgumentException(
                "You must pass one argument to StoreRoundTask execute!");
        int numRight = scores[0].getNumberCorrectAnswers();
        int numWrong = scores[0].getNumberWrongAnswers();
        int maxScore = scores[0].total() * GameActivity.TIME_LIMIT;
        int score = scores[0].score();

        MapQuizDbHelper dbHelper = MapQuizDbHelper.getHelper(mApp.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MapQuizContract.Rounds.COLUMN_NAME_MODE, mMode.getModeId());
        values.put(MapQuizContract.Rounds.COLUMN_NAME_NUM_RIGHT, numRight);
        values.put(MapQuizContract.Rounds.COLUMN_NAME_NUM_WRONG, numWrong);
        values.put(MapQuizContract.Rounds.COLUMN_NAME_USER_ID, getUserId());
        values.put(MapQuizContract.Rounds.COLUMN_NAME_SCORE, score);
        values.put(MapQuizContract.Rounds.COLUMN_NAME_RATIO, (float) score / (float) maxScore);
        db.insert(MapQuizContract.Rounds.TABLE_NAME,
                MapQuizContract.NULLABLE_COLUMN,
                values);
        return null;
    }

    private long getUserId() {
        SharedPreferences prefs = mApp.getSharedPreferences(
                MapQuizContract.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        long userId = prefs.getLong(MapQuizContract.USER_ID_KEY, -1);
        if (userId ==  -1) throw new IllegalStateException("Current user doesn't have an ID stored on the device!");
        return userId;
    }
}
