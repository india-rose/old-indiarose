package org.indiarose.indiarosetimer.database.base;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.api.IndiagramManager;
import org.indiarose.lib.model.Indiagram;

import org.indiarose.indiarosetimer.modele.Categorie;
import org.indiarose.indiarosetimer.modele.TimerModele;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccessBaseTimer {

	private static final int VERSION_BDD = 1;
	private static final String BDD_NAME = "timers.db";

	private SQLiteDatabase bdd;    
	private BaseTimer mBase;

	public AccessBaseTimer(Context context){
		mBase = new BaseTimer(context, BDD_NAME, null, VERSION_BDD);
	}

	/**
	 * Open Database
	 */
	public void open(){
		bdd = mBase.getWritableDatabase();
		mBase.onCreate(bdd);
	}

	/**
	 * Close Database
	 */
	public void close(){
		bdd.close();
	}

	/**
	 * Get Database
	 * 
	 * @return - Database
	 */
	public SQLiteDatabase getBdd(){
		return this.bdd;
	}

	/**
	 * Insert a new Timer
	 * 
	 * @param timer -  Timer 
	 * 
	 * @return - Timer's id
	 */
	public long insertTimer(TimerModele timer){
		ContentValues values  = new ContentValues();
		values.put(BaseTimer.COL_TYPE_CHRONO, timer.getTypeChrono());
		values.put(BaseTimer.COL_TIME, timer.getTimeSeconde());
		values.put(BaseTimer.COL_COLOR_GRADUATION_TEXT, timer.getGraduationTextColor());
		values.put(BaseTimer.COL_COLOR_GRADUATION_TRAIT, timer.getGraduationTraitColor());
		values.put(BaseTimer.COL_COLOR_PRINCIPAL, timer.getColorPrincipal());
		values.put(BaseTimer.COL_COLOR_SECONDAIRE, timer.getColorSecondaire());
		values.put(BaseTimer.COL_NAME_TIMER, timer.getName());

		if(timer.getPath_consigne() != null){
			values.put(BaseTimer.COL_PATH_CONSIGNE, timer.getPath_consigne());
		}else{
			values.put(BaseTimer.COL_PATH_CONSIGNE, "");

		}
		return bdd.insert(BaseTimer.TABLE_TIMER,null, values);
	}

	/**
	 * 
	 * @param timerModele 
	 */
	public void uptadeTimer(TimerModele timerModele) {
		ContentValues args = new ContentValues();
		args.put(BaseTimer.COL_TYPE_CHRONO,timerModele.getTypeChrono());
		args.put(BaseTimer.COL_TIME,timerModele.getTimeSeconde());
		args.put(BaseTimer.COL_COLOR_GRADUATION_TEXT,timerModele.getGraduationTextColor());
		args.put(BaseTimer.COL_COLOR_GRADUATION_TRAIT,timerModele.getGraduationTraitColor());
		args.put(BaseTimer.COL_COLOR_PRINCIPAL,timerModele.getColorPrincipal());
		args.put(BaseTimer.COL_COLOR_SECONDAIRE,timerModele.getColorSecondaire());
		args.put(BaseTimer.COL_NAME_TIMER, timerModele.getName());
		if(timerModele.getPath_consigne() != null){
			args.put(BaseTimer.COL_PATH_CONSIGNE, timerModele.getPath_consigne());
		}else{
			args.put(BaseTimer.COL_PATH_CONSIGNE, "");

		}
		bdd.update(BaseTimer.TABLE_TIMER, args,BaseTimer.COL_ID+"="+timerModele.getId(), null);		

	}

	/**
	 *  Return Timer with id
	 * @param id - Timer's Id
	 * 
	 * @return - Timer 
	 */
	public TimerModele getTimer(int id){

		String requete = "SELECT * FROM " + BaseTimer.TABLE_TIMER + " WHERE " + BaseTimer.COL_ID + "="+id;
		Cursor cursor = bdd.rawQuery(requete,null);

		if(cursor != null){
			cursor.moveToFirst();	
			TimerModele timer = cursorToTimer(cursor);
			cursor.close();
			return timer;
		}

		return null;
	}

	/**
	 * Return the last timer in database
	 * 
	 * @return - Last Timer
	 */
	public long returnLastTimer(){

		String query = "SELECT "+ BaseTimer.COL_ID +" FROM "+ BaseTimer.TABLE_TIMER +" ORDER BY "+BaseTimer.COL_ID+" DESC LIMIT 1";
		Cursor cursor;
		cursor = bdd.rawQuery(query,null);

		if (cursor != null && cursor.moveToFirst()){
			long id =  cursor.getLong(0);
			cursor.close();
			return id;
		}

		return -1 ;
	}

	/**
	 * Delete timer with id
	 * 
	 * @param id - Timer's Id
	 */
	public void deleteTimer(int id_timer,int id_categorie){
		bdd.delete(BaseTimer.TABLE_CONTIENT,BaseTimer.COL_ID_TIMER+"="+id_timer +" AND " + BaseTimer.COL_ID_CATEGORY+"="+id_categorie,null);
		bdd.delete(BaseTimer.TABLE_TIMER,BaseTimer.COL_ID+"="+id_timer,null);
	}


	public List<TimerModele> getAllTimer(){

		String requete = "SELECT DISTINCT * FROM " + BaseTimer.TABLE_TIMER + " ;";

		Cursor cursor = bdd.rawQuery(requete, null);
		return ConvertCursorToListTimer(cursor);
	}


	/**
	 * Convert a curson to Timer
	 * 
	 * @param cursor - Cursor with information of database for timer
	 * 
	 * @return - Timer with information of cursor
	 */
	protected TimerModele cursorToTimer(Cursor cursor){		
		TimerModele timer = new TimerModele(
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_ID)),  
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_TYPE_CHRONO)),  
				cursor.getFloat(cursor.getColumnIndex(BaseTimer.COL_TIME)),
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_COLOR_GRADUATION_TEXT)),
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_COLOR_GRADUATION_TRAIT)),
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_COLOR_PRINCIPAL)),
				cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_COLOR_SECONDAIRE)),
				cursor.getString(cursor.getColumnIndex(BaseTimer.COL_NAME_TIMER)),
				cursor.getString(cursor.getColumnIndex(BaseTimer.COL_PATH_CONSIGNE))
				);
		return timer;
	}

	protected Categorie cursorToCategory(Cursor cursor,IndiagramManager indiaManager){
		Categorie categorie = new Categorie();
		categorie.setId(cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_ID)));
		categorie.setIndiagram(indiaManager.getIndiagramByPath(cursor.getString(cursor.getColumnIndex(BaseTimer.COL_PATH_INDIAGRAM))));
		categorie.setType_categorie(cursor.getInt(cursor.getColumnIndex(BaseTimer.COL_TYPE_CATEGORY)));
		return categorie;
	}

	/**
	 * Get all timer
	 * 
	 * @param cursor
	 * @return
	 */
	protected List<TimerModele> ConvertCursorToListTimer(Cursor cursor) {

		ArrayList<TimerModele> liste = new ArrayList<TimerModele>();

		// If list is empty
		if (cursor.getCount() == 0) 
			return liste;

		// First item
		cursor.moveToFirst();

		// For all items
		do {
			TimerModele cont = cursorToTimer(cursor);
			liste.add(cont);
		} while (cursor.moveToNext());

		cursor.close();
		return liste;
	}

	protected List<Categorie> ConvertCursorToListCategory(Cursor cursor,IndiagramManager indiaManager) {

		ArrayList<Categorie> liste = new ArrayList<Categorie>();

		// If list is empty
		if (cursor.getCount() == 0) 
			return liste;

		// First item
		cursor.moveToFirst();

		// For all items
		do {
			Categorie cat = cursorToCategory(cursor, indiaManager);
			liste.add(cat);
		} while (cursor.moveToNext());

		cursor.close();
		return liste;
	}

	// CATEGORY

	public long insertCategory(Categorie categorie){
		ContentValues values = new ContentValues();
		values.put(BaseTimer.COL_TYPE_CATEGORY, categorie.getType_categorie());
		values.put(BaseTimer.COL_PATH_INDIAGRAM, categorie.getIndiagram().filePath);
		Log.e("File Path save",categorie.getIndiagram().filePath);
		return bdd.insert(BaseTimer.TABLE_CATEGORY, null, values);
	}

	public Categorie getCategory(int id_category,IndiagramManager indiaManager){

		String requete = "SELECT * FROM " + BaseTimer.TABLE_CATEGORY + " WHERE " + BaseTimer.COL_ID + "="+id_category;
		Cursor cursor = bdd.rawQuery(requete,null);

		if(cursor != null){
			cursor.moveToFirst();	
			Categorie categorie = cursorToCategory(cursor,indiaManager);
			cursor.close();
			List<TimerModele> timers = timersByCategory(id_category);
			if(timers != null)
				categorie.setTimers(timers);
			return categorie;
		}

		return null;
	}

	public List<Categorie> getAllCategory(IndiagramManager indiaManager) {
		String requete = "SELECT DISTINCT * FROM " + BaseTimer.TABLE_CATEGORY + " ;";
		Cursor cursor = bdd.rawQuery(requete, null);
		return ConvertCursorToListCategory(cursor,indiaManager);
	}

	public long insertContient(int id_timer,int id_categorie){
		ContentValues values = new ContentValues();
		values.put(BaseTimer.COL_ID_TIMER, id_timer);
		values.put(BaseTimer.COL_ID_CATEGORY, id_categorie);

		return bdd.insert(BaseTimer.TABLE_CONTIENT, null, values);
	}

	public List<TimerModele> timersByCategory(int id_category){

		String requete = "SELECT DISTINCT * FROM " + BaseTimer.TABLE_TIMER + " WHERE " + BaseTimer.TABLE_TIMER +"."+BaseTimer.COL_ID+ " IN (" 
				+ "SELECT DISTINCT " + BaseTimer.TABLE_CONTIENT+"."+BaseTimer.COL_ID_TIMER+ " FROM " + BaseTimer.TABLE_CONTIENT +" WHERE " + BaseTimer.TABLE_CONTIENT+"."
				+ BaseTimer.COL_ID_CATEGORY
				+ " = " + id_category
				+");";

		Cursor cursor = bdd.rawQuery(requete,null);

		if(cursor != null){
			cursor.moveToFirst();
			return ConvertCursorToListTimer(cursor);
		}

		return null;
	}


}
