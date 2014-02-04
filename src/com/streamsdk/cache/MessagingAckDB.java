package com.streamsdk.cache;

import java.util.ArrayList;
import java.util.List;

import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.chat.domain.IM;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class MessagingAckDB {

	private static final String DATABASE_NAME = "mrdb";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private SQLiteStatement mInsertStmt;
	private DatabaseHelper helper;
    private static final String MINSERT = "insert into mrdb (id, type, fromuser, touser, message, fileid, duration) values (?,?,?,?,?,?,?)";
	
	
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
              StreamXMPPMessage xmpp = new StreamXMPPMessage();
              xmpp.setId(id);
              xmpp.setType(type);
              xmpp.setFrom(fromuser);
              xmpp.setTo(touser);
              xmpp.setMessage(message);
              xmpp.setFileId(fileid);
              xmpp.setDuration(duration);
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
		mInsertStmt.executeInsert();
		
	}
	
	public void insertVideoImage(IM im, String fileId){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "video");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, "");
		mInsertStmt.bindString(6, fileId);
		mInsertStmt.bindString(7, "");
		mInsertStmt.executeInsert();
		
	}
	
	
	public void insertTextMessage(IM im){
		
		mInsertStmt.bindString(1, String.valueOf(im.getChatTime()));
		mInsertStmt.bindString(2, "text");
		mInsertStmt.bindString(3, im.getFrom());
		mInsertStmt.bindString(4, im.getTo());
		mInsertStmt.bindString(5, im.getChatMessage());
		mInsertStmt.bindString(6, "");
		mInsertStmt.bindString(7, "");
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
			 db.execSQL("CREATE TABLE mrdb (id TEXT PRIMARY KEY, type TEXT, fromuser TEXT, touser TEXT, message TEXT,fileid TEXT, duration TEXT)");
		}

		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			 db.execSQL("DROP TABLE IF EXISTS mrdb");
	         onCreate(db);
		}
    }    
	
	
}
