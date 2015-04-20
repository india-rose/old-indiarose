package org.indiarose.indiarosetimer.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseTimer extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_TIMER = "table_timer";
	public static final String TABLE_CATEGORY = "table_category";
	public static final String TABLE_CONTIENT = "table_contient";

	// Base colums
	public static final String COL_ID = "ID";	
	public static final String COL_PAS = "PAS";
	public static final String COL_RADIUS = "RADIUS";
	public static final String COL_TYPE_CHRONO = "TYPECHRONO";
	public static final String COL_TIME = "TIMESECONDE";
	public static final String COL_COLOR_GRADUATION_TEXT="GRADUATIONTEXT";
	public static final String COL_COLOR_GRADUATION_TRAIT="GRADUATIONTRAIT";
	public static final String COL_COLOR_PRINCIPAL="COLORPRINCIPAL";
	public static final String COL_COLOR_SECONDAIRE="COLORSECONDAIRE";
	public static final String COL_NAME_TIMER = "COLNAMETIMER";
	public static final String COL_PATH_CONSIGNE = "COLPATHCONSIGNE";
	
	public static final String COL_TYPE_CATEGORY="TYPECATEGORY";
	public static final String COL_PATH_INDIAGRAM="PATHINDIA";

	public static final String COL_ID_TIMER = "IDTIMER";
	public static final String COL_ID_CATEGORY= "IDCATEGORY";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_TIMER +" , " + TABLE_CATEGORY + " , " + TABLE_CONTIENT;

	private static final String CREATE_TABLE_TIMER = "CREATE TABLE if not exists  " + TABLE_TIMER + " ("
			+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_TIME + " FLOAT NOT NULL, "
			+ COL_TYPE_CHRONO + " INTEGER NOT NULL, "
			+ COL_COLOR_GRADUATION_TEXT + " INTEGER NOT NULL, "
			+ COL_COLOR_GRADUATION_TRAIT + " INTEGER NOT NULL, "
			+ COL_COLOR_PRINCIPAL + " INTEGER NOT NULL, "
			+ COL_COLOR_SECONDAIRE + " INTEGER NOT NULL, "
			+ COL_NAME_TIMER + " TEXT NOT NULL, "
			+ COL_PATH_CONSIGNE + " TEXT NOT NULL"			
			+ " );";


	private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COL_TYPE_CATEGORY + " INTEGER NOT NULL, "
			+ COL_PATH_INDIAGRAM + " TEXT NOT NULL"
			+ " );";

	private static final String CREATE_TABLE_CONTIENT = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTIENT + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_ID_TIMER + " INTEGER NOT NULL, "
			+ COL_ID_CATEGORY + " INTEGER NOT NULL"
			+ " );";

	public BaseTimer(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTIENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		*/
		db.execSQL(CREATE_TABLE_TIMER);
		db.execSQL(CREATE_TABLE_CATEGORY);
		db.execSQL(CREATE_TABLE_CONTIENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > DATABASE_VERSION) {
			db.execSQL(SQL_DELETE_ENTRIES);
			onCreate(db);
		}
	}

}