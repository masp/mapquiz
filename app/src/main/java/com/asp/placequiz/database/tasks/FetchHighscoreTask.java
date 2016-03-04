package com.asp.placequiz.database.tasks;

import static com.asp.placequiz.database.MapQuizContract.Rounds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.asp.placequiz.database.MapQuizDbHelper;
import com.asp.placequiz.game.Mode;
import com.asp.placequiz.game.ScorePair;

import java.util.ArrayList;
import java.util.List;

public abstract class FetchHighscoreTask extends AsyncTask<Void, Void, ScorePair> {
    private Context mContext;
    private Mode mMode;

    public FetchHighscoreTask(Context context, Mode mode) {
        mContext = context;
        mMode = mode;
    }

    @Override
    protected final ScorePair doInBackground(Void... voids) {
        MapQuizDbHelper dbHelper = MapQuizDbHelper.getHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String modeWhereClause = Rounds.COLUMN_NAME_MODE + "=" + mMode.getModeId();
        String numRightWhereClause = "(SELECT MAX(" + Rounds.COLUMN_NAME_RATIO + ") FROM " + Rounds.TABLE_NAME +
                " WHERE " + modeWhereClause + ")";
        String query = "SELECT " + Rounds.COLUMN_NAME_NUM_RIGHT + "," +
                                    Rounds.COLUMN_NAME_NUM_WRONG + "," +
                                    Rounds.COLUMN_NAME_RATIO + "," +
                                    Rounds.COLUMN_NAME_SCORE +
                " FROM " + Rounds.TABLE_NAME +
                " WHERE " + modeWhereClause +
                " AND " + Rounds.COLUMN_NAME_RATIO + " = " + numRightWhereClause;
        Cursor c = db.rawQuery(query, null);
        try {
            int scoreColumn = c.getColumnIndex(Rounds.COLUMN_NAME_SCORE);
            int rightColumn = c.getColumnIndex(Rounds.COLUMN_NAME_NUM_RIGHT);
            int wrongColumn = c.getColumnIndex(Rounds.COLUMN_NAME_NUM_WRONG);
            if (rightColumn == -1 || wrongColumn == -1) {
                throw new IllegalStateException("Unable to find the score columns!");
            }
            if (c.getCount() > 0) {
                List<ScorePair> scores = new ArrayList<ScorePair>();
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    ScorePair score = new ScorePair(c.getInt(scoreColumn), c.getInt(rightColumn),
                            c.getInt(wrongColumn));
                    scores.add(score);
                }
                return getHighestScore(scores);
            }
        } finally {
            c.close();
        }
        return new ScorePair(0, 0, 0);
    }

    private ScorePair getHighestScore(List<ScorePair> scores) {
        ScorePair highest = new ScorePair(0, 0, 0);
        for (ScorePair score : scores) {
            if (score.asPercent() > highest.asPercent())
                highest = score;
            else if (score.asPercent() == highest.asPercent() && score.correct() > highest.correct())
                highest = score;
        }
        return highest;
    }
}
