package com.streamsdk.cache;

import java.util.ArrayList;
import java.util.List;

import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.chat.domain.IM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class MessagingAckDB {

	private static final String DATABASE_NAME = "mrdb";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
	private DatabaseHelper helper;
    private static final String MINSERT = "insert into mrdb (id, type, fromuser, touser, message, fileid, duration, tid, lat, lon, address) values (?,?,?,?,?,?,?,?,?,?,?)";
	
	
	public MessagingAckDB(Context context){
		helper = new DatabaseHelper(context);
    	db = helper.getWritableDatabase();
    	mInsertStmt = db.compileStatement(MINSERT);
	}
	
	public List<StreamXMPPMessage> getIMAcks(){
		List<StreamXMPPMessage> acks = new ArrayList<StreamXMPPMessage>();
		Cursor c = db.rawQuery("SELECT * FROM mrdb", null);
		if  (c!= null && c.moveToFirst()) {
            do {
              String id = c.getString(0);
              String type = c.getString(1);
              String fromuser = c.getString(2);
              String touser = c.getString(3);
              String message = c.getString(4);
              String fileid = c.getString(5);
              String duration = c.getString(6);
              String tid = c.getString(7);
              String lat = c.getString(8);
              String lon = c.getString(9);
              String address = c.getString(10);
              StreamXMPPMessage xmpp = new StreamXMPPMessage();
              xmpp.setId(id);
              xmpp.setType(type);
              xmpp.setFrom(fromuser);
              xmpp.setTo(touser);
              xmpp.setMessage(message);
              xmpp.setFileId(fileid);
              xmpp.setDuration(duration);
              xmpp.setThumbnailId(tid);
              xmpp.setLatitude(lat);
              xmpp.setLongitude(lon);
              xmpp.setAddress(address);
              
              acks.add(xmpp);
             	
            }while(c.moveToNext());
		}
		
		return acks;
	}
	
	public void insertVoiceMessage(IM im, String fileId){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "voice");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, "");
		mInsertStmt.bindString(6, fileId);
		mInsertStmt.bindString(7, String.valueOf(im.getRecordingTime()));
		mInsertStmt.bindString(8, "");
		mInsertStmt.bindString(9, "");
		mInsertStmt.bindString(10, "");
		mInsertStmt.executeInsert();
	
	}
	
	public void insertMapMessage(IM im, String fileId){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "map");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, "");
		mInsertStmt.bindString(6, fileId);
		mInsertStmt.bindString(7, "");
		mInsertStmt.bindString(8, im.getLatitude());
		mInsertStmt.bindString(9, im.getLongitude());
		mInsertStmt.bindString(10, im.getAddress());
		mInsertStmt.executeInsert();
		
	}
	
	
	public void insertImageMessage(IM im, String fileId){

		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "photo");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, "");
		mInsertStmt.bindString(6, fileId);
		mInsertStmt.bindString(7, im.getTimeout());
		mInsertStmt.bindString(8, "");
		mInsertStmt.bindString(9, "");
		mInsertStmt.bindString(10, "");
		mInsertStmt.executeInsert();
		
	}
	
	public void updateVideoImageTid(IM im, String tid){
		ContentValues cvs = new ContentValues();
		cvs.put("tid", tid);
		int result = db.update("mrdb", cvs, "id=?", new String[]{String.valueOf(im.getChatTime())});
    	Log.i("update tid result", String.valueOf(result));
	}
	
	public void insertVideoImage(IM im, String fileId){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "video");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, "");
		mInsertStmt.bindString(6, fileId);
		mInsertStmt.bindString(7, im.getTimeout());
		mInsertStmt.bindString(8, "");
		mInsertStmt.bindString(9, "");
		mInsertStmt.bindString(10, "");
		mInsertStmt.executeInsert();
		
	}
	
	
	public void insertTextMessage(IM im, String body){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "text");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, body);
		mInsertStmt.bindString(6, "");
		mInsertStmt.bindString(7, "");
		mInsertStmt.bindString(8, "");
		mInsertStmt.bindString(9, "");
		mInsertStmt.bindString(10, "");
		mInsertStmt.executeInsert();
	
	}
	
	public void delete(String chatId){
		int result = db.delete("mrdb", "id=?", new String[]{chatId});
        Log.i("", String.valueOf(result));
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE mrdb (id TEXT PRIMARY KEY, type TEXT, fromuser TEXT, touser TEXT, message TEXT,fileid TEXT, duration TEXT, tid TEXT, lat TEXT, lon TEXT, address TEXT)");
		}

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 if (oldVersion == 1 && newVersion == 2){
				 db.execSQL("ALTER TABLE mrdb ADD COLUMN lat TEXT");
				 db.execSQL("ALTER TABLE mrdb ADD COLUMN lon TEXT");
				 db.execSQL("ALTER TABLE mrdb ADD COLUMN address TEXT");	 
			 }else{
				 db.execSQL("DROP TABLE IF EXISTS mrdb");
		         onCreate(db);	 
			 }
		}
    }    
	
	
}
