package com.streamsdk.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class InvitationDB {

	private static final String DATABASE_NAME = "inivitationdb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
    private static final String MINSERT = "insert into inivitationdb (username) values (?)";
    private DatabaseHelper helper;
    
	public InvitationDB(Context context){
		helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
	}
	
	public void close(){
		helper.close();
	}
	
	public void deleteAll(){
		int result = db.delete(DATABASE_NAME, null, null);
	 	Log.i("delete all" + DATABASE_NAME, String.valueOf(result));
	}
	
	public void insert(String userName){
		    mInsertStmt.bindString(1, userName);
		    mInsertStmt.executeInsert();
	}
	
	public List<String> getInvitations(){
		
		List<String> names = new ArrayList<String>();
    	Cursor c = db.rawQuery("SELECT username FROM inivitationdb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
            	
            	String name = c.getString(0);
            	names.add(name);
            	
            }while (c.moveToNext());
            c.close();
        } 
	
		return names;
		
	}
    
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE inivitationdb (username TEXT PRIMARY KEY)");
		}

		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			db.execSQL("DROP TABLE IF EXISTS frienddb");
			onCreate(db);
		}
	}
}
