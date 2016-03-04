package com.asp.placequiz.database.tasks;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.asp.placequiz.database.DbOperations;
import com.asp.placequiz.database.MapQuizContract;
import com.asp.placequiz.database.MapQuizDbHelper;

public class FetchUserIDTask extends AsyncTask<String, Void, Long> {
    private Application mApplication;

    public FetchUserIDTask(Application application) {
        this.mApplication = application;
    }

    @Override
    protected Long doInBackground(String... emails) {
        String email = emails[0];
        MapQuizDbHelper dbHelper = MapQuizDbHelper.getHelper(mApplication);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long userId = DbOperations.getUserId(db, email);
        if (userId != -1) return userId;

        ContentValues values = new ContentValues();
        values.put(MapQuizContract.UserEntry.COLUMN_NAME_USER_EMAIL_HASH, email.hashCode());
        values.put(MapQuizContract.UserEntry.COLUMN_NAME_LAST_ACCESS, System.currentTimeMillis());
        userId = db.insert(MapQuizContract.UserEntry.TABLE_NAME,
                MapQuizContract.NULLABLE_COLUMN,
                values);

        if (userId == -1) throw new IllegalStateException("An error has occured in trying to insert " +
                "the user " + email + " into the database!");
        return userId;
    }

    @Override
    protected void onPostExecute(Long userId) {
        SharedPreferences prefs = mApplication.getSharedPreferences(
                MapQuizContract.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(MapQuizContract.USER_ID_KEY, userId);
        editor.apply();
    }
}
