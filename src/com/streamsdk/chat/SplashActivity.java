package com.streamsdk.chat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	
	private void deleteFiles(){
		StreamUser user = new StreamUser();
		List<StreamFile> sf = user.getListOfUserStreamFiles();
		for (StreamFile stf : sf) {
			stf.delete();
			Log.i("delete file", stf.getId());
		}
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
	     final Activity activity = this;
		 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		 final String userName;
		 final String password;
		 if (settings != null){
            userName = (String)settings.getString("username", "");
            password = (String)settings.getString("password", "");
		 }else{
			userName = null;
			password = null;
		 }
		 
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
						
					}
				}
	
		  } ,this);
	
	
	}
}
