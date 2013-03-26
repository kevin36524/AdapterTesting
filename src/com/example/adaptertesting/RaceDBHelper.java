package com.example.adaptertesting;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RaceDBHelper extends SQLiteOpenHelper {
	  public static final String RACE_TABLE = "raceTable";
	  public static final String COLUMN_ID = "_id";
	  public static final String RACE_NAME = "raceName";
	  public static final String PLAYER_NAME = "pName";
	  public static final String LAP_TIMES = "lapTimes";
	  public static final String BEST_TIMES = "bestTimes";
	  

	  private static final String DATABASE_NAME = "race.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + RACE_TABLE + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + RACE_NAME 
	      + " text not null, " + PLAYER_NAME + " text not null, " + LAP_TIMES + " text not null, " + 
	       BEST_TIMES + " REAL not null " +  ");";

	  public RaceDBHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(RaceDBHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + RACE_TABLE);
	    onCreate(db);
	  }
	  
	  public void addTimes (String raceName, JSONObject timeObj) {
		  SQLiteDatabase db = this.getWritableDatabase();
		  ContentValues values;
		  JSONObject tempObj;
		  Iterator<?> keys = timeObj.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            try {
					if( timeObj.get(key) instanceof JSONObject ){
						values = new ContentValues();
						tempObj = timeObj.getJSONObject(key);
						values.put(RACE_NAME, raceName);
						values.put(PLAYER_NAME, key);
						values.put(LAP_TIMES, tempObj.getString("lapTimes"));
						values.put(BEST_TIMES, tempObj.getDouble("bestTime"));
						db.insert(RACE_TABLE, null, values);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        db.close();
	  }
	  
	  public Cursor getTimes (String raceName) {
		  Log.i("KevinDebug", "inside getTimes");
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor retCursor = db.query(RACE_TABLE, null, RACE_NAME + "=?", new String [] {raceName}, null, null, BEST_TIMES, null);
		  retCursor.moveToFirst();
		  if (retCursor != null) { 
				try {
					String retNotes = retCursor.getString(retCursor.getColumnIndex(PLAYER_NAME));
					Log.i("KevinDebug", retNotes);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Log.i("KevinDebug", "retCursor seems null");
			}
		 
		  db.close();
		  return retCursor;
	  }
	  
	  public void deleteRows (String raceName) {
		  SQLiteDatabase db = this.getWritableDatabase();
		  db.delete(RACE_TABLE, RACE_NAME + "=?", new String[] {raceName});
		  db.close();
	  }
	  
	  /*
	public void addNote (int id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Log.i("KevinDebug", "Adding the note " + note + " to id " + id);
		
		values.put(COLUMN_ID, id);
		values.put(COLUMN_NOTES, note);
		
		db.insert(TABLE_NOTES, null, values);
		db.close();
	}
	
	public void initialAdd (JSONArray dbArray) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		JSONObject tempObj;
		
		for (int i = 0; i < dbArray.length(); i++) {
			values = new ContentValues();
		   try {
			   tempObj = dbArray.getJSONObject(i);
			   JSONArray tempNameArr = tempObj.names();
			   String key;
			   
			   for (int j=0; j < tempNameArr.length(); j++) {
				   key = tempNameArr.getString(j);
				   
				   if (key.equals(COLUMN_ID)){
					   values.put(key, tempObj.getLong(key));
				   } else {
					   values.put(key, tempObj.getString(key));
				   }
			   }
		   } catch (JSONException e) {
			   e.printStackTrace();
		   }	
		   
		   if (values.size() > 1) {
			   db.insert(TABLE_NOTES, null, values);
		   }
		   
		}
		
		db.close();
		DATABASE_SET = true;
	}
	
	public String getNote (int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String retNotes = null;
		
		Cursor retCursor = db.query(TABLE_NOTES, new String [] {COLUMN_NOTES}, COLUMN_ID + "=?", new String [] {""+id}, null, null, null);
		
		retCursor.moveToFirst();
		
		if (retCursor != null) { 
			try {
				retNotes = retCursor.getString(retCursor.getColumnIndex(COLUMN_NOTES));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
		
		return retNotes;
	}
	
	public String getNotesFromPhoneNumber (String phNum) {
		SQLiteDatabase db = this.getReadableDatabase();
		String retNotes = null;
				
		Cursor tempCursor = db.query(TABLE_NOTES, new String [] {COLUMN_NOTES}, COLUMN_PH_NUMBER + " LIKE ? ", new String [] {"%" + phNum.substring(phNum.length() - 8)}, null, null, null);
		
		tempCursor.moveToFirst();
		try {
			retNotes = tempCursor.getString(tempCursor.getColumnIndex(COLUMN_NOTES));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		db.close();
		
		Log.i("KevinDebug", " Notes from getNotesFromPhoneNumber are :- " + retNotes);
		return retNotes;
	}
	
	public void updateNote(long id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Log.i("KevinDebug", "Updating the note " + note + " to id " + id);
		
		values.put(COLUMN_NOTES, note);
		db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String [] {"" + id});
		db.close();
	}
	*/

}
