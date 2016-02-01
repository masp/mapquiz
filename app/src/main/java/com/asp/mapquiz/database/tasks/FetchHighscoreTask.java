package com.asp.mapquiz.database.tasks;

import static com.asp.mapquiz.database.MapQuizContract.Rounds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.asp.mapquiz.database.MapQuizContract;
import com.asp.mapquiz.database.MapQuizDbHelper;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.game.ScorePair;

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
                " AND " + Rounds.COLUMN_NAME_RATIO + " = " + numRightWhereClause +
                " LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            int scoreColumn = c.getColumnIndex(Rounds.COLUMN_NAME_SCORE);
            int rightColumn = c.getColumnIndex(Rounds.COLUMN_NAME_NUM_RIGHT);
            int wrongColumn = c.getColumnIndex(Rounds.COLUMN_NAME_NUM_WRONG);
            if (rightColumn == -1 || wrongColumn == -1) {
                throw new IllegalStateException("Unable to find the score columns!");
            }
            if (c.getCount() > 0) {
                return new ScorePair(c.getInt(scoreColumn), c.getInt(rightColumn),
                        c.getInt(wrongColumn));
            }
        } finally {
            c.close();
        }
        return new ScorePair(0, 0, 0);
    }
}
