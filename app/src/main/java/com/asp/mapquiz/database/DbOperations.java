package com.asp.mapquiz.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class DbOperations {
    /**
     * Using the database provided finds the email's unique user ID.
     * @param db The database to use. <b>Must be open!</b>
     * @param email The email of the user to use as a search key.
     * @return The user ID for the given email or -1 if none found in the database.
     */
    public static long getUserId(SQLiteDatabase db, String email) {
        if (db.isOpen()) {
            String[] projection = {
                    MapQuizContract.UserEntry._ID,
                    MapQuizContract.UserEntry.COLUMN_NAME_USER_EMAIL_HASH
            };
            String selection = MapQuizContract.UserEntry.COLUMN_NAME_USER_EMAIL_HASH +
                    " = '" + email.hashCode() + "'";
            Cursor c = db.query(
                    MapQuizContract.UserEntry.TABLE_NAME,  // Table name
                    projection,                            // Columns to select
                    selection,                             // Where statement
                    null,
                    null,                                  // no grouping ignore
                    null,                                  // no grouping ignore
                    null);                                 // no ordering ignore
            try {
                int columnIndex = c.getColumnIndex(MapQuizContract.UserEntry._ID);
                if (columnIndex == -1)
                    throw new IllegalStateException("Unable to find the user ID column!");
                c.moveToFirst();
                if (c.getCount() > 0) {
                    long userId = c.getLong(columnIndex);
                    Log.d("DBTEST", "Found user with unique id " + userId);
                    return userId;
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }
}
