package com.streamsdk.chat;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.stream.api.StreamCallback;
import com.stream.api.StreamFile;
import com.stream.api.StreamSession;
import com.stream.api.StreamUser;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.xmpp.ApplicationXMPPListener;

public class SplashActivity extends Activity{

	private int retryCount = 0;
	Activity activity;
	String userName;
	String password;
    Handler handler;
	
	private void deleteFiles(){
		StreamUser user = new StreamUser();
		List<StreamFile> sf = user.getListOfUserStreamFiles();
		for (StreamFile stf : sf) {
			stf.delete();
			Log.i("delete file", stf.getId());
		}
	}
	
	public void connect(){
		
		 
	     StreamSession.authenticate(ApplicationInstance.APPID, ApplicationInstance.cKey, ApplicationInstance.sKey, new StreamCallback() {
				public void result(boolean succeed, String errorMessage) {
					if (succeed){
						//deleteFiles();
						if (userName != null && password != null && !userName.equals("") && !password.equals("")){
							try {
							  if (!StreamXMPP.getInstance().isConnected()){	
								 StreamXMPP.getInstance().login(ApplicationInstance.APPID + userName, password);
								 ApplicationInstance.getInstance().setCheckConnection(true);
								 ApplicationInstance.getInstance().setLoginName(userName);
								 ApplicationInstance.getInstance().setPassword(password);
								 ApplicationXMPPListener.getInstance().addListenerForAllUsers();
						    	 ApplicationXMPPListener.getInstance().addFileReceiveListener();
						    
						      }
							} catch (Exception e) {
								Log.i("", e.getMessage());
							}
							Intent intent = new Intent(activity, MyFriendsActivity.class);
							startActivity(intent);
							finish();
					    }else{
				        	Intent intent = new Intent(activity, FirstPageActivity.class);
							startActivity(intent);
							finish();
						}
						
					}else{
					   if (retryCount < 5){
								try {
									Thread.sleep(10000);
									retryCount++;
									Log.i("retry", "retry connection " + retryCount);
									connect();
								} catch (InterruptedException e) {}
					   }else{
						   handler.sendEmptyMessage(0);
					   }
					}
				}
	
		  } ,this);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.splash_layout);
		 
		 EmojiParser.getInstance(getApplicationContext()).getEmoMap();
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	     activity = this;
		 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		 if (settings != null){
            userName = (String)settings.getString("username", "");
            password = (String)settings.getString("password", "");
		 }else{
			userName = null;
			password = null;
		 }
		 handler = new Handler(new Handler.Callback() {
				public boolean handleMessage(Message message) {
					showAlertDialog();
					return true;
				}
		    });
		 connect();
	}
	
	private void showAlertDialog(){		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
				.setMessage("No network connection, Please check")
				.setCancelable(false)
				.setNegativeButton("OK I Check",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						finish();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
}
