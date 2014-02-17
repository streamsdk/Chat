package com.streamsdk.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class ChatBackgroundDB {

	private static final String DATABASE_NAME = "chatdb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
    private static final String MINSERT = "insert into chatdb (username, resource, path) values (?,?,?)";
    private DatabaseHelper helper;
	
	
	public ChatBackgroundDB(Context context){
		helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
	}
	
	public void updateChatBackground(String userName, String resource, String path){		
		Object backImage = selectChatBackground(userName);
		if (backImage  != null){
			ContentValues values = new ContentValues();
			values.put("resource", resource);
			values.put("path", path);
			int result = db.update("chatdb", values, "username=?", new String[]{userName});
	    	Log.i("update chat background", String.valueOf(result));
		}else{
			insertChatDB(userName, resource, path);
		}
	}
	
	
	public Object selectChatBackground(String userName){
		
		Cursor c = db.rawQuery("SELECT * FROM chatdb WHERE username=?", new String[] {userName});
		if  (c!= null && c.moveToFirst()){
			 String resource = c.getString(1);
			 String path = c.getString(2);
			 if (!resource.equals(""))
				 return Integer.parseInt(resource);
			 else
				 return path;
		}
		return null;
	}
	
	
	public void insertChatDB(String userName, String resource, String path){
		
		mInsertStmt.bindString(1, userName);
		mInsertStmt.bindString(2, resource);
		mInsertStmt.bindString(3, path);
		mInsertStmt.executeInsert();	
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		 
	        DatabaseHelper(Context context) 
	        {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	        public void onCreate(SQLiteDatabase db) {
				 db.execSQL("CREATE TABLE chatdb (username TEXT PRIMARY KEY, resource TEXT, path TEXT)");
			}

			public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
				 db.execSQL("DROP TABLE IF EXISTS chatdb");
		         onCreate(db);
			}
	 }

}
