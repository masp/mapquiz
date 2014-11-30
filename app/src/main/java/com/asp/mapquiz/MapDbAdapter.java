package com.asp.mapquiz;

import java.io.File;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * Gas record database access helper class. Defines the basic operations
 * for the Gas Record application.
 * 
 * 
 */
public class MapDbAdapter {

    

    private static final String TAG = "MapDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    
    

    private static final String DATABASE_NAME = "mapquiz";
    private static final String ROUND_TABLE = "round";
    private static final String GUESS_TABLE = "guess";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            createDatabase(db);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
            upgradeDatabase(db);
        }
    }
    private static void createDatabase(SQLiteDatabase db){
    	db.execSQL("CREATE TABLE " + GUESS_TABLE + " ("
                + Guess._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Guess.ROUND_ID + " INTEGER NOT NULL, "
                + Guess.CORRECT + " INTEGER NOT NULL, "
                + Guess.COORD_LAT + " DOUBLE NOT NULL, "
                + Guess.COORD_LON + " DOUBLE NOT NULL, "
                + Guess.STATE1 + " STRING NOT NULL, "
                + Guess.STATE2 + " STRING NOT NULL, "
                + Guess.STATE3 + " STRING NOT NULL, "
                + Guess.STATE4 + " STRING NOT NULL, "
                + Guess.SELECTED + " INTEGER NOT NULL, "
                + Guess.DATETIME + " INTEGER NOT NULL, "
                + Guess.TIME2GUESS + " DOUBLE NOT NULL "
                + ");");
        
        db.execSQL("CREATE TABLE " + ROUND_TABLE + " ("
                + Round._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Round.PLAYER_ID + " INTEGER NOT NULL, "
                + Round.QUIZ_TYPE + " INTEGER NOT NULL, "
                + Round.GUESS1 + " INTEGER NOT NULL, "
                + Round.GUESS2 + " INTEGER NOT NULL, "
                + Round.GUESS3 + " INTEGER NOT NULL, "
                + Round.GUESS4 + " INTEGER NOT NULL, "
                + Round.GUESS5 + " INTEGER NOT NULL, "
                + Round.GUESS6 + " INTEGER NOT NULL, "
                + Round.GUESS7 + " INTEGER NOT NULL "
                + ");");
    }
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public MapDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the gasrecord database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public MapDbAdapter open() throws SQLException {
    	
    		mDbHelper = new DatabaseHelper(mCtx);
    	
        mDb = mDbHelper.getWritableDatabase();
        mDb.rawQuery("PRAGMA journal_mode=DELETE", null);
//        Log.v("GasRecord","Gas Database version= "+mDb.getVersion());
//        Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
//        String sqliteVersion = "";
//        while(cursor.moveToNext()){
//           sqliteVersion += cursor.getString(0);
//        }
//        cursor.close();
//        Log.v("GasRecord","Gas Database SQLite version= "+sqliteVersion);
        return this;
    }
    
    public void close() {
    	if(mDb!=null){
    		 if (mDb.isOpen())mDb.close();
       
    	}
    }


    /**
     * Create a new Guess record using the values provided. If the Guess is
     * successfully created return the new rowId for that Guess, otherwise return
     * a -1 to indicate failure.
     * 
     * @param initialValues of the Guess
     * @return rowId or -1 if failed
     */
    public long createGuess(ContentValues initialValues) {
    	long Id = 0;
        
        try {
        Id=mDb.insertOrThrow(GUESS_TABLE, null, initialValues);
       
//           Toast.makeText(this.mCtx, "Row " + Id + " inserted." , Toast.LENGTH_SHORT).show();
            } catch (SQLException ex){
            	Toast.makeText(this.mCtx, "SQLException " + ex , Toast.LENGTH_SHORT).show();
            }
        return Id;
    }
    

    
   
    
    
    
    /**
     * Create a new Round record using the values provided. If the Round is
     * successfully created return the new rowId for that Round, otherwise return
     * a -1 to indicate failure.
     * 
     * @param initialValues of the Round
     * @return rowId or -1 if failed
     */
    public long createRound(ContentValues initialValues) {
    	long Id = 0;
        
        try {
        Id=mDb.insertOrThrow(ROUND_TABLE, null, initialValues);
       
 //          Toast.makeText(this.mCtx, "Row " + Id + " inserted." , Toast.LENGTH_SHORT).show();
            } catch (SQLException ex){
//            	Toast.makeText(this.mCtx, "SQLException " + ex , Toast.LENGTH_SHORT).show();
            	Log.v("GasRecord","Create Round error= "+ex);
            }
        return Id;
    }

    /**
     * Delete the Guess with the given rowId
     * 
     * @param rowId id of Guess to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteGuess(long rowId) {

        return mDb.delete(GUESS_TABLE, Guess._ID + "=" + rowId, null) > 0;
    }
    /**
     * Delete the Round with the given rowId
     * 
     * @param rowId id of Round to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRound(long rowId) {

        return mDb.delete(ROUND_TABLE, Round._ID + "=" + rowId, null) > 0;
    }


    /**
     * Return a Cursor over the list of all Rounds in the database using just nickname
     * 
     * @return Cursor over all Rounds
     */
    public Cursor fetchAllRounds() {

        return mDb.query(ROUND_TABLE, null, null, null, null, null, null);
    }
    /**
     * Return a Cursor over the list of all Guesss in the database using all fields
     * 
     * @return Cursor over all Guesss
     */
    public Cursor fetchAllGuesssAll() {

        Cursor mCursor =

                mDb.query(GUESS_TABLE, null, null,
                        null, null, null, null);
        
       
        return mCursor;

    }
    /**
     * Return a Cursor over the list of all Guesss in the database using all fields
     * 
     * @return Cursor over all Guesss
     */
    public Cursor fetchAllGuesssDateRange(long startDate, long endDate) {
    	String selection=Guess.DATETIME+ ">=" + startDate + " AND " + Guess.DATETIME+ "<=" + endDate;
    	String orderBy=Guess.DATETIME+ " ASC ";
        Cursor mCursor =

                mDb.query(GUESS_TABLE, null, selection,
                        null, null, null, orderBy);
        
       
        return mCursor;

    }
    /**
     * Return a Cursor over the list of all Guesss in the database using all fields
     * 
     * @return Cursor over all Guesss
     */
    
    public Cursor fetchRoundGuesssDateRange(long RoundID, long startDate, long endDate) {
    	
				
				
    	String selection=Guess.ROUND_ID+ "=" + String.valueOf(RoundID)+ " AND "  + Guess.DATETIME+ ">=" + startDate + " AND " + Guess.DATETIME+ "<=" + endDate;
//    	Toast.makeText(this.mCtx, "Selection " + selection, Toast.LENGTH_SHORT).show();
    	String orderBy=Guess.DATETIME+ " ASC ";
    	Cursor mCursor =

                mDb.query(GUESS_TABLE, null, selection,
                        null, null, null, orderBy);
        
       
        return mCursor;

    }
    /**
     * Return a Cursor over the list of all Rounds in the database using all fields
     * 
     * @return Cursor over all Rounds
     */
    public Cursor fetchAllRoundsAll() {

        Cursor mCursor =

                mDb.query(ROUND_TABLE, null, null,
                        null, null, null, null);
        
       
        return mCursor;

    }

    /**
     * Return a Cursor positioned at the Guess that matches the given rowId
     * 
     * @param rowId id of Guess to retrieve
     * @return Cursor positioned to matching Guess, if found
     * @throws SQLException if Guess could not be found/retrieved
     */
    public Cursor fetchGuess(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, GUESS_TABLE, null, Guess._ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Return a Cursor positioned at the Round that matches the given rowId
     * 
     * @param rowId id of Round to retrieve
     * @return Cursor positioned to matching Round, if found
     * @throws SQLException if Round could not be found/retrieved
     */
    public Cursor fetchRound(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ROUND_TABLE, null, Round._ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

/**
 * Return a Content values for the Round that matches the given rowId
 * 
 * @param rowId id of Round to retrieve
 * @return Cursor positioned to matching Round, if found
 * @throws SQLException if Round could not be found/retrieved
 *
 */
public ContentValues fetchRoundData(long rowId) throws SQLException {
	ContentValues curVal=new ContentValues();
    Cursor mCursor =

            mDb.query(true, ROUND_TABLE, null, Round._ID + "=" + rowId, null,
                    null, null, null, null);
    if (mCursor != null) {
        mCursor.moveToFirst();
        curVal.put(Round._ID, mCursor.getLong(mCursor.getColumnIndex(Round._ID)));
        curVal.put(Round.PLAYER_ID, mCursor.getLong(mCursor.getColumnIndex(Round.PLAYER_ID)));
        curVal.put(Round.QUIZ_TYPE, mCursor.getInt(mCursor.getColumnIndex(Round.QUIZ_TYPE)));
        curVal.put(Round.GUESS1, mCursor.getString(mCursor.getColumnIndex(Round.GUESS1)));
        curVal.put(Round.GUESS2, mCursor.getString(mCursor.getColumnIndex(Round.GUESS2)));
        curVal.put(Round.GUESS3, mCursor.getString(mCursor.getColumnIndex(Round.GUESS3)));
        curVal.put(Round.GUESS4, mCursor.getString(mCursor.getColumnIndex(Round.GUESS4)));
        curVal.put(Round.GUESS5, mCursor.getString(mCursor.getColumnIndex(Round.GUESS5)));
        curVal.put(Round.GUESS6, mCursor.getString(mCursor.getColumnIndex(Round.GUESS6)));
        curVal.put(Round.GUESS7, mCursor.getString(mCursor.getColumnIndex(Round.GUESS7)));

    }
    
    
    return curVal;

}
/**
 * Return a Content values for the Guess that matches the given rowId
 * 
 * @param rowId id of Guess to retrieve
 * @return Cursor positioned to matching Guess, if found
 * @throws SQLException if Guess could not be found/retrieved
 *
 */
public ContentValues fetchGuessData(long rowId) throws SQLException {
	ContentValues curVal=new ContentValues();
    Cursor mCursor =

    	mDb.query(true, GUESS_TABLE, null, Guess._ID + "=" + rowId, null,
                null, null, null, null);
    if (mCursor != null) {
        mCursor.moveToFirst();
        curVal.put(Guess._ID, mCursor.getLong(mCursor.getColumnIndex(Guess._ID)));
        curVal.put(Guess.ROUND_ID, mCursor.getLong(mCursor.getColumnIndex(Guess.ROUND_ID)));
        curVal.put(Guess.CORRECT, mCursor.getInt(mCursor.getColumnIndex(Guess.CORRECT)));
        curVal.put(Guess.DATETIME, mCursor.getLong(mCursor.getColumnIndex(Guess.DATETIME)));
        curVal.put(Guess.COORD_LAT, mCursor.getDouble(mCursor.getColumnIndex(Guess.COORD_LAT)));
        curVal.put(Guess.COORD_LON, mCursor.getDouble(mCursor.getColumnIndex(Guess.COORD_LON)));
        curVal.put(Guess.STATE1, mCursor.getString(mCursor.getColumnIndex(Guess.STATE1)));
        curVal.put(Guess.STATE2, mCursor.getString(mCursor.getColumnIndex(Guess.STATE2)));
        curVal.put(Guess.STATE3, mCursor.getString(mCursor.getColumnIndex(Guess.STATE3)));
        curVal.put(Guess.STATE4, mCursor.getString(mCursor.getColumnIndex(Guess.STATE4)));
        curVal.put(Guess.SELECTED, mCursor.getInt(mCursor.getColumnIndex(Guess.SELECTED)));
        curVal.put(Guess.TIME2GUESS, mCursor.getDouble(mCursor.getColumnIndex(Guess.TIME2GUESS)));

    }
    
    
    return curVal;

}
    /**
     * Update the Guess using the details provided. The Guess to be updated is
     * specified using the rowId, and it is altered to use the
     * values passed in
     * 
     * @param rowId id of Guess to update
     * @param ?? value to set VALUE to
     *
     * @return true if the Guess was successfully updated, false otherwise
     */
    public boolean updateGuess(long rowId, ContentValues args) {
        
        return mDb.update(GUESS_TABLE, args, Guess._ID + "=" + rowId, null) > 0;
    }
    /**
     * Update the Round using the details provided. The Round to be updated is
     * specified using the rowId, and it is altered to use the
     * values passed in
     * 
     * @param rowId id of Round to update
     * @param ?? value to set VALUE to
     *
     * @return true if the Round was successfully updated, false otherwise
     */
    public boolean updateRound(long rowId, ContentValues args) {
        
        return mDb.update(ROUND_TABLE, args, Round._ID + "=" + rowId, null) > 0;
    }
    


 /*
    public long getRoundId(Long playerid) {
	
		// get the cursor for the Round with the nickname, if any
		long id=0;
		String selection = Round.PLAYER_ID + " = ? " ;
		String[] selectionArgs = new String[] {
				playerid
				
		};
		String orderBy = Round._ID + " ASC ";
		Log.v("GasRecord", "nickname: "+playerid);
		Log.v("GasRecord", "selection: "+selection);
		Log.v("GasRecord", "selectionArgs: "+selectionArgs[0]);
		Log.v("GasRecord", "orderBy: "+orderBy);
		Cursor c = mDb.query(ROUND_TABLE, null,
		 selection, selectionArgs, null, null, orderBy, "1");

		if (c.getCount() == 1) {
			c.moveToFirst();
			int index=c.getColumnIndex(Round._ID);
			if(index>-1){
				id=c.getLong(index);
				Log.v("GasRecord", "id: "+id);
			}
		}
	
		Log.v("GasRecord", "returning id: "+id);
	return id;
}
*/

    private String getDateTime() {
    	long inttime=System.currentTimeMillis();
    	CharSequence inFormat="yyyy-MM-dd_hh-mm-ss";
    	String s= (String) DateFormat.format(inFormat, inttime);
    	 
    	return s;
    }

	
	public static boolean upgradeDatabase() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File("/data/data/com.asp.gasrecord/databases/" + DATABASE_NAME), null);
		boolean result = upgradeDatabase(db);
		db.close();
		return result;
	}

	public static boolean upgradeDatabase(SQLiteDatabase db) {
		
		int oldVersion = 2;
		
			upgradeDatabase(db, oldVersion, oldVersion + 1);
			return true;
		
	}
	
	private static void upgradeDatabase(SQLiteDatabase db, final int oldVersion, final int newVersion) {
		try {
			StringBuilder sb = new StringBuilder();
			if (oldVersion == 1) {
				//sb.append("ALTER TABLE ").append(GUESS_TABLE).append(" ADD COLUMN ").append(Guess.ECONOMY_UNITS).append(" INTEGER;");
				//db.execSQL(sb.toString());
				return;
			} 
			if (oldVersion == 2) {
				//sb.setLength(0);
				//sb.append("ALTER TABLE ").append(ROUND_TABLE).append(" ADD COLUMN ").append(Round.ENGINE).append(" TEXT;");
				//db.execSQL(sb.toString());

				return;
			} 
			sb.setLength(0);
			sb.append("DROP TABLE IF EXISTS ").append(GUESS_TABLE).append(";");
			db.execSQL(sb.toString());

			

			createDatabase(db);
		} catch (SQLiteException e) {
			
		}
	}
}
