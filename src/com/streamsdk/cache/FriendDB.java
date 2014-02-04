package com.streamsdk.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.streamsdk.chat.domain.FriendRequest;

public class FriendDB {

	private static final String DATABASE_NAME = "frienddb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
    private Set<String> localUsernames;
    private static final String MINSERT = "insert into frienddb (username,status) values (?,?)";
    private DatabaseHelper helper;
    
    //add some comments here
    public FriendDB(Context context){
    	helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
    }
    
    public void close(){
        helper.close();	
    }
    
    public void insert(String userName, String status){
	    mInsertStmt.bindString(1, userName);
	    mInsertStmt.bindString(2, status);
	    mInsertStmt.executeInsert();
    }
    
    public boolean userNameExists(String userName){
    	
    	Cursor c = db.rawQuery("SELECT username,status FROM frienddb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
            	
            	String name = c.getString(0);
            	if (name.equals(userName))
            		return true;
            	
            }while (c.moveToNext());
            c.close();
        } 
		
		return false;
    }
    
    public void delete(String userName){
    	int result = db.delete("frienddb", "username=?", new String[]{userName});
        Log.i("", String.valueOf(result));
    }
    
    public void update(String userName, String status){
    	ContentValues values = new ContentValues();
    	values.put("status", status);
    	int result = db.update("frienddb", values, "username=?", new String[]{userName});
    	Log.i("", String.valueOf(result));
    }
    
    public void syncUpdate(List<FriendRequest> requests){
    	
    	getFriendRequestList();
    	for (FriendRequest request : requests){
    		if (localUsernames.contains(request.getFriendName()))
    			update(request.getFriendName(), request.getStatus());
    		else
    			insert(request.getFriendName(), request.getStatus());
    	}
    	
    }
    
    public List<FriendRequest> getFriendRequestList(){
    	
    	List<FriendRequest> names = new ArrayList<FriendRequest>();
    	localUsernames = new HashSet<String>();
    	Cursor c = db.rawQuery("SELECT username,status FROM frienddb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
            	
            	String name = c.getString(0);
            	String status  = c.getString(1);
            	FriendRequest fr = new FriendRequest();
            	fr.setFriendName(name);
            	fr.setStatus(status);
            	names.add(fr);
            	localUsernames.add(name);
            	
            }while (c.moveToNext());
            c.close();
        } 
	
		return names;
    }
    
    public String[] getFriendsArray(){
    	List<String> names = new ArrayList<String>();
    	Cursor c = db.rawQuery("SELECT username,status FROM frienddb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
            	
            	String name = c.getString(0);
            	String status  = c.getString(1);
            	if (status.equals("friend"))
            	     names.add(name);
            	
            }while (c.moveToNext());
            c.close();
        } 
		
		String str[] = new String[names.size()];
		int index = 0;
		for (String n : names){
			str[index] = n.toLowerCase();
			index++;
		}
    	return str;
    	
    }
    
    
    public List<String> getFriends(){
    	
    	List<String> names = new ArrayList<String>();
    	Cursor c = db.rawQuery("SELECT username,status FROM frienddb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
            	
            	String name = c.getString(0);
            	String status = c.getString(1);
            	if (status.equals("friend"))
            	     names.add(name);
            	
            }while (c.moveToNext());
            c.close();
        } 
		
		return names;
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE frienddb (username TEXT PRIMARY KEY,status TEXT)");
		}

		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			 db.execSQL("DROP TABLE IF EXISTS frienddb");
	         onCreate(db);
		}
    }

	
    
}
