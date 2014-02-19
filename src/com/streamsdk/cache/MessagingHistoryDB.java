package com.streamsdk.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.domain.IM;

public class MessagingHistoryDB {

	
	private static final String DATABASE_NAME = "mhdb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
    private static final String MINSERT = "insert into mhdb (id, chattime, type, content, fromuser, touser,recordingtime,duration,viewed) values (?,?,?,?,?,?,?,?,?)";
	private DatabaseHelper helper;
    
	public MessagingHistoryDB(Context context){
		helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
	}
	
	public void deleteAll(){
		int result = db.delete(DATABASE_NAME, null, null);
	 	Log.i("delete all" + DATABASE_NAME, String.valueOf(result));
	}
	
	public void close(){
		helper.close();
	}
	
	public void updateViewStatus(String chatTime){
		ContentValues values = new ContentValues();
    	values.put("viewed", "YES");
    	int result = db.update("mhdb", values, "chattime=?", new String[]{chatTime});
    	Log.i("", String.valueOf(result));
	}
	
	public void delete(String fromUser, String toUser){
		
		Cursor c = db.rawQuery("SELECT * FROM mhdb WHERE (fromuser=? AND touser=?) OR (fromuser=? AND touser=?)", new String[] {fromUser, toUser, toUser, fromUser});
		if  (c!= null && c.moveToFirst()) {
            do {
            	  String type = c.getString(2);
            	  String content = c.getString(3);
               	  if (!type.equals("text")){
            		 File file = new File(content); 
                	 if (file.exists()){
                         file.delete();
                	 }	  
            	  }
            	 
            }while (c.moveToNext());
            c.close();
		}
		int result = db.delete("mhdb", "(fromuser=? AND touser=?) OR (fromuser=? AND touser=?)", new String[]{fromUser, toUser, toUser, fromUser});
	    Log.i("", String.valueOf(result));
	}
	
	public List<IM> getIMHistoryForUser(String fromUser, String toUser){
		List<IM> history = new ArrayList<IM>();
		Cursor c = db.rawQuery("SELECT * FROM mhdb WHERE (fromuser=? AND touser=?) OR (fromuser=? AND touser=?)", new String[] {fromUser, toUser, toUser, fromUser});
		if  (c!= null && c.moveToFirst()) {
            do {
              String time = c.getString(1);
              String type = c.getString(2);
              if (type == null)
            	  continue;
              String content = c.getString(3);
              String from = c.getString(4);
              String to = c.getString(5);
              String recordingTime = c.getString(6);
              String duration = c.getString(7);
              String viewed = c.getString(8);
              IM im = new IM();
              if (time != null && !time.isEmpty())
                  im.setChatTime(Long.parseLong(time));
              im.setFrom(from);
              im.setTo(to);
              if (ApplicationInstance.getInstance().getLoginName().equals(from))
            	  im.setSelf(true);
              if (!duration.equals("")){
            	  if (type.equals("video"))
            		  im.setVideo(true);
            	  if (type.equals("photo"))
            		  im.setImage(true);
              	  im.setTimeout(duration);
            	  im.setDisappear(true);
            	  im.setViewed(viewed);
              }
              if (!type.equals("text")){
            	   File file = new File(content); 
            	   if (!file.exists() && !im.isDisappear())
            		   continue;
              }
              if (type.equals("text")){
            	  im.setChatMessage(content);
              }
              if (type.equals("photo") || (im.isDisappear() && im.getViewed().equals("NO")) && !type.equals("video")){
            	  im.setImage(true);
            	  im.storeSendImage(content);
            	  if (!im.isSelf())
            		  im.setReceivedFilePath(content);
              }
              if (type.equals("video") || (im.isDisappear() && im.getViewed().equals("NO")) && !type.equals("photo")){
            	  im.setVideo(true);
            	  im.storeSendImage(content);
            	  if (!im.isSelf())
            		  im.setReceivedFilePath(content);
              }
              if (type.equals("voice")){
            	  im.setVoice(true);
            	  im.setRecordingTime(Integer.parseInt(recordingTime));
            	  im.setVoiceImFileName(content);
              }
             
              history.add(im);
            }while (c.moveToNext());
            c.close();
        } 
		return history;
	}
	
	public synchronized void insert(IM im){
		String primaryKey = im.getPrimaryKey();
		if (primaryKey.equals(""))
			im.setPrimaryKey(String.valueOf(im.getChatTime()));
		String type = "";
		String content = "";
		String recordingtime = "";
		String viewd = "NO";
		String duration = im.getTimeout();
		if (im.isImage()){
			type = "photo";
			if (im.isSelf())
				content = im.getSelfSendImagePath();
			else
			    content = im.getReceivedFilePath();
		}else if (im.isVideo()){
			type = "video";
			if (im.isSelf())
				content = im.getSelfSendImagePath();
			else
			    content = im.getReceivedFilePath();
		}else if (im.isVoice()){
			type = "voice";
			content = im.getVoiceImFileName();
			recordingtime = String.valueOf(im.getRecordingTime());
		}else{
			type = "text";
			content = im.getChatMessage();
		}
		
		mInsertStmt.bindString(1, im.getPrimaryKey());
	    mInsertStmt.bindString(2, String.valueOf(im.getChatTime()));
	    mInsertStmt.bindString(3, type);
	    mInsertStmt.bindString(4, content);
	    mInsertStmt.bindString(5, im.getFrom());
	    mInsertStmt.bindString(6, im.getTo());
	    mInsertStmt.bindString(7, recordingtime);
	    mInsertStmt.bindString(8, duration);
	    mInsertStmt.bindString(9, viewd);
	    mInsertStmt.executeInsert();
  }
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE mhdb (id TEXT PRIMARY KEY, chattime TEXT, type TEXT, content TEXT, fromuser TEXT, touser TEXT,recordingtime TEXT, duration TEXT, viewed TEXT)");
		}

		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			 db.execSQL("DROP TABLE IF EXISTS mhdb");
	         onCreate(db);
		}
    }    
}
