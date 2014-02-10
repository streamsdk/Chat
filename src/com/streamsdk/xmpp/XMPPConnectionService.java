package com.streamsdk.xmpp;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.stream.api.JsonUtils;
import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.api.StreamSession;
import com.stream.xmpp.StreamXMPP;
import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.ParseMsgUtil;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.handler.MessageHistoryHandler;

public class XMPPConnectionService extends Service{

	private Timer timer;
	private static boolean started = false;
	public static final String HISTORY = "messaginghistory";
	public static final String BODY_PREFIX = "message.body.";
	
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (!started){
			  timer.schedule(new  ReconnectXMPPService(), 10000, 20000);
			  timer.schedule(new StatusSendService(), 20000, 1000 * 60 * 2);
			  timer.schedule(new ResendIMService(), 30000, 1000 * 60 * 1);
			  started = true;
		}
		return START_STICKY;
	}
	
	public void onDestroy(){
		super.onDestroy();
		timer.cancel();
		started = false;
	}
	
	public void onCreate() {
		timer = new Timer();
	}	
	
	private void reintiDB(){
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	}
	
	private class ResendIMService extends TimerTask{
		
		public void run(){
			MessagingAckDB mackdb = ApplicationInstance.getInstance().getMessagingAckDB();
			if (mackdb == null){
				reintiDB();
			}
			
			List<StreamXMPPMessage> messages = ApplicationInstance.getInstance().getMessagingAckDB().getIMAcks();
			for (final StreamXMPPMessage message : messages){
				String chatTime = message.getId();
				long messgeSentTime = Long.parseLong(chatTime);
				long now = System.currentTimeMillis();
				long diff = (now - messgeSentTime)/(1000 * 60);
				if (diff >= 2 && diff <= 5){
					String messageBody = "";
					Message packet = new Message();
					if (message.getType().equals("text")){
					   messageBody = JsonUtils.buildPlainTextMessage(message.getMessage(), message.getFrom(), message.getId());
					}
					if (message.getType().equals("voice")){
						packet.setProperty("streamsdk.filetransfer", message.getFileId());
						messageBody = JsonUtils.buildVoiceMessaging(message.getDuration(), message.getFileId(), message.getFrom(), Long.parseLong(message.getId()));	
					}
					if (message.getType().equals("photo") || message.getType().equals("video")){
					   packet.setProperty("streamsdk.filetransfer", message.getFileId());
					   messageBody = JsonUtils.buildImageVideoMessaing(message.getFileId(), message.getType(), message.getFrom(), message.getDuration(), Long.parseLong(message.getId()));
					}
						
					packet.setTo(ApplicationInstance.APPID + message.getTo() + ApplicationInstance.HOST_PREFIX);
			        packet.setBody(messageBody);
					StreamXMPP.getInstance().sendPacket(packet);
				}
				
				if (diff > 5){
					StreamObject history = new StreamObject();
		            history.setId(message.getTo() + HISTORY);
		            String messageBody = "";
					if (message.getType().equals("text")){
					   messageBody = JsonUtils.buildPlainTextMessage(message.getMessage(), message.getFrom(), message.getId());
					}
					if (message.getType().equals("voice")){
						messageBody = JsonUtils.buildVoiceMessaging(message.getDuration(), message.getFileId(), message.getFrom(), Long.parseLong(message.getId()));	
					}
					if (message.getType().equals("photo") || message.getType().equals("video")){
					   messageBody = JsonUtils.buildImageVideoMessaing(message.getFileId(), message.getType(), message.getFrom(), message.getDuration(), Long.parseLong(message.getId()));
					}
		            String key = String.valueOf(System.currentTimeMillis());
		            history.put(key, BODY_PREFIX + messageBody);
		            Log.i("send as history", messageBody);
		            history.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
						    if (succeed){
						    	 ApplicationInstance.getInstance().getMessagingAckDB().delete(message.getId());
						    }
						}
					});
					
				}
				
			}			
		}		
	}
	
	
	private class StatusSendService extends TimerTask{
		
	  public void run(){
			
		  Log.i("xmpp service", "send status");
		  try{	
			 StreamXMPP.getInstance().sendAvStatus(ApplicationInstance.APPID  + ApplicationInstance.getInstance().getLoginName() + ApplicationInstance.HOST_PREFIX);
		  }catch(Throwable t){
			 Log.i("", "no connection send status"); 
		  }
	   }
		
	}
	
	
	
	private class ReconnectXMPPService extends TimerTask {
		
		public void run() {
			
			if (!StreamXMPP.getInstance().isConnected()){
				Log.i("xmpp service", "check connection");
				boolean auth = StreamSession.authenticate(ApplicationInstance.APPID, ApplicationInstance.cKey,ApplicationInstance.sKey, null);
			    if (auth) {
				 	Log.i("xmpp service", "reconnect");
					SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
					final String userName;
					final String password;
					if (settings != null) {
						userName = (String) settings.getString("username", "");
						password = (String) settings.getString("password", "");
					} else {
						userName = null;
						password = null;
					}
					try {
						StreamXMPP.getInstance().login(ApplicationInstance.APPID + userName, password);
						ApplicationInstance.getInstance().setCheckConnection(true);
						ApplicationInstance.getInstance().setLoginName(userName);
						ApplicationInstance.getInstance().setPassword(password);
						ApplicationXMPPListener.getInstance().addListenerForAllUsers();
						ApplicationXMPPListener.getInstance().addFileReceiveListener();
						new Thread(new MessageHistoryHandler(ApplicationInstance.getInstance().getLoginName(),getApplicationContext(), null)).start();

					} catch (XMPPException e) {
						Log.i("xmpp service", e.getMessage());
					}
				
			     }
		   }
		}
	}
}
