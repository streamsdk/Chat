package com.streamsdk.cache;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class MessagingCountDB {

	
	private static final String DATABASE_NAME = "mcdb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
    private static final String MINSERT = "insert into mcdb (id, toUser) values (?,?)";
	private DatabaseHelper helper;
    
	public MessagingCountDB(Context context){
		helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
  
	}
	
	public void deleteAll(){
		int result = db.delete(DATABASE_NAME, null, null);
	 	Log.i("delete all" + DATABASE_NAME, String.valueOf(result));
	}
	
	public Map<String, String> getMessagingCount(String to){
		
		Cursor c = db.rawQuery("SELECT * FROM mcdb WHERE toUser=?", new String[] {to});
	    Map<String, String> mCounts = null;
	    
		if  (c!= null && c.moveToFirst()) {
			 mCounts = new HashMap<String, String>();
            do {
            	 String mId = c.getString(0);
                 String toUser = c.getString(1);
            	 mCounts.put(mId, toUser);
            }while (c.moveToNext());
            c.close();
		}
		
		return mCounts;
		
	}
	
	public int delete(String userName){
	    	int result = db.delete("mcdb", "toUser=?", new String[]{userName});
	        Log.i("delete result", String.valueOf(result));
	        return result;
	}
	
	public void insert(String mId, String to){
		
		 mInsertStmt.bindString(1, mId);
		 mInsertStmt.bindString(2, to);
		 mInsertStmt.executeInsert();
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE mcdb (id TEXT PRIMARY KEY, toUser TEXT)");
		}

		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			 db.execSQL("DROP TABLE IF EXISTS mcdb");
	         onCreate(db);
		}
    }    
}
