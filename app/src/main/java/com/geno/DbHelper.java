package com.geno;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	// Debug code
	private final String TAG = "DbHelper";
	private final boolean D = true;
	
	public static final String DB_NAME = "Token.db";
	public static final String TABLE_NAME = "token";
	public static final int _ID = 0;
	public static final int USER_ID = _ID + 1;
	public static final int USER_PASSWORD = USER_ID + 1;
	public static final int USER_TYPE = USER_PASSWORD + 1;
	public static final int APP_PW = USER_PASSWORD + 1;
	public static final int TOKEN = APP_PW + 1;
	
	private static final String CREATE_DB_COMMAND = 
			"Create table if not exists " + TABLE_NAME + "(" +
			"_id integer primary key autoincrement, " +
			"user_id text, " +
			"user_pw text, " +
			"user_type text, " +
			"user_app_pw text, " +
			"token text" + ")";

	private static DbHelper dbHelper;
	private static SQLiteDatabase mDB;

	private DbHelper(Context context) {
		super(context, DB_NAME, null, 1);		
	}
	
	public static final DbHelper getInstance(Context context) {
		initialize(context);
		
		return dbHelper;
	}
	
	private static void initialize(Context context)
	{
		if( dbHelper == null )
		{
			try
			{
				dbHelper = new DbHelper(context);
				mDB = dbHelper.getWritableDatabase();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_DB_COMMAND);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
	
	public SQLiteDatabase getDatabase() {
		return mDB;
	}
	
	public boolean closeDatabase() {
		if(mDB.isOpen()) {
			mDB.close();
			
			return true;
		} else {
			return true;
		}
	}
}