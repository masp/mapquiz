package com.asp.placequiz.database;

import android.provider.BaseColumns;

public final class MapQuizContract {
    private MapQuizContract() {}

    public static final String SHARED_PREFERENCES_NAME = "mapquiz.prefs";
    public static final String USER_ID_KEY = "userid";

    // For the SQLite database hack
    public static final String NULLABLE_COLUMN = null;

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String FLOAT_TYPE = " REAL";
    public static final String COMMA_SEP = ",";

    public static final String[] SQL_CREATE_TABLES = new String[] {
            UserEntry.SQL_CREATE_USER_TABLE,
            Rounds.SQL_CREATE_ROUND_TABLE
    };

    public static final String[] SQL_DELETE_TABLES = new String[] {
            UserEntry.SQL_DELETE_USER_TABLE,
            Rounds.SQL_DELETE_ROUND_TABLE
    };

    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_NAME_USER_EMAIL_HASH = "email_hash";
        public static final String COLUMN_NAME_LAST_ACCESS = "lastaccess";

        public static final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_USER_EMAIL_HASH + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_ACCESS + INTEGER_TYPE +
                " )";

        public static final String SQL_DELETE_USER_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class Rounds implements BaseColumns {
        public static final String TABLE_NAME = "rounds";

        public static final String COLUMN_NAME_USER_ID = "userid";
        public static final String COLUMN_NAME_MODE = "mode";
        public static final String COLUMN_NAME_NUM_RIGHT = "num_right";
        public static final String COLUMN_NAME_NUM_WRONG = "num_wrong";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_RATIO = "score_ratio";

        public static final String SQL_CREATE_ROUND_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_MODE + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_NUM_RIGHT + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_NUM_WRONG + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_SCORE + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_RATIO + FLOAT_TYPE + ")";

        public static final String SQL_DELETE_ROUND_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
