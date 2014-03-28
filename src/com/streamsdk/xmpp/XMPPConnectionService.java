package com.streamsdk.xmpp;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import com.streamsdk.cache.ChatBackgroundDB;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.MyFriendsActivity;
import com.streamsdk.chat.R;
import com.streamsdk.chat.domain.OnlineOffineUpdate;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.handler.MessageHistoryHandler;

public class XMPPConnectionService extends Service implements NotificationInterface{

	private Timer timer;
	private static boolean started = false;
	public static final String HISTORY = "messaginghistory";
	public static final String BODY_PREFIX = "message.body.";
	private String userName;
	private String password;
	
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!started){
			  timer.schedule(new  ReconnectXMPPService(), 5000, 15000);
			  timer.schedule(new StatusSendService(), 30000, 1000 * 60 * 2);
			  timer.schedule(new ResendIMService(), 90000, 1000 * 60 * 1);
			  timer.schedule(new CheckMessaingHistoryService(this), 70000, 1000 * 60 * 2);
			  timer.schedule(new OnlineOfflineStatusUpdate(), 1000, 1000 * 40);
			  ApplicationXMPPListener.getInstance().addNotifier("service", this);
			  EmojiParser.getInstance(this).initiMap(this);
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
		ApplicationXMPPListener.getInstance().addNotifier("service", this);
	}	
	
	private void reintiDB(){
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ChatBackgroundDB cdb = new ChatBackgroundDB(this);
		 ApplicationInstance.getInstance().setChatBackgroundDB(cdb);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	}
	
	private class CheckMessaingHistoryService extends TimerTask{
		
		private NotificationInterface notificationInterface;
		public CheckMessaingHistoryService(NotificationInterface ni){
			this.notificationInterface = ni;
		}
		
		public void run(){
		  if (loggedIn()){
			 MessageHistoryHandler mhh = new MessageHistoryHandler(ApplicationInstance.getInstance().getLoginName(), getApplicationContext());
			 mhh.setNotificationInterface(notificationInterface);
			 mhh.run();
		  }
		}
	}
	
	private class OnlineOfflineStatusUpdate extends TimerTask{
		public void run(){
			OnlineOffineUpdate oou = ApplicationInstance.getInstance().getOnlineOfflineUpdate();
			if (ApplicationInstance.getInstance().isVisiable()){
				if (oou == null){
				   Log.i("update to online", "oou is null");
		           updateOnline();        			
				}else{
					long lastUpdateTime = oou.getLastUpdatedTime();
					long diff = (System.currentTimeMillis() -  lastUpdateTime) / (1000 * 60);
					if (diff > 3 || oou.isUpdateToOffline()){
						updateOnline();
						Log.i("update to online", "oou is greater than 3 and just come back from offline");
					}
				}
			}else{
				if (oou != null && oou.isUpdateToOnline()){
					updateOffline();
					Log.i("update to offine", "oou goes not background");
				}
			}
		}
		
		private void updateOnline(){
			if (userName != null && !userName.equals("")){
				final StreamObject so = new StreamObject();
				so.setId(userName);
				so.put("online", "YES");
				so.put("lastseen", String.valueOf(System.currentTimeMillis()));
				so.updateObjectInBackground(new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					    if (succeed){
					    	OnlineOffineUpdate oou = new OnlineOffineUpdate();
					    	oou.setLastUpdatedTime(System.currentTimeMillis());
					    	oou.setUpdateToOffline(false);
					    	oou.setUpdateToOnline(true);
					    	ApplicationInstance.getInstance().setOnlineOfflineUpdate(oou);
					    }else{
					    	so.deleteObject();
					    	so.createNewStreamObject();
					    }
					}
				});
			}
		}
		
		private void updateOffline(){
			if (userName != null && !userName.equals("")){
				final StreamObject so = new StreamObject();
				so.setId(userName);
				so.put("online", "NO");
				so.updateObjectInBackground(new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					    if (succeed){
					    	OnlineOffineUpdate oou = new OnlineOffineUpdate();
					    	oou.setLastUpdatedTime(System.currentTimeMillis());
					    	oou.setUpdateToOffline(true);
					    	oou.setUpdateToOnline(false);
					    	ApplicationInstance.getInstance().setOnlineOfflineUpdate(oou);
					    }else{
					    	so.deleteObject();
					    	so.createNewStreamObject();
					    }
					}
				});
			}
		}
		
	}
	
	private class ResendIMService extends TimerTask{
		
		public void run(){
		  if (loggedIn()){
			 List<StreamXMPPMessage> messages = ApplicationInstance.getInstance().getMessagingAckDB().getIMAcks();
			 for (final StreamXMPPMessage message : messages){
				String chatTime = message.getId();
				if (chatTime == null){
					Log.i("vatal error", "chat time null");
					continue;
				}
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
					   messageBody = JsonUtils.buildImageVideoMessaing(message.getFileId(), message.getType(), message.getFrom(), message.getDuration(), Long.parseLong(message.getId()), message.getThumbnailId());
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
					   messageBody = JsonUtils.buildImageVideoMessaing(message.getFileId(), message.getType(), message.getFrom(), message.getDuration(), Long.parseLong(message.getId()), message.getThumbnailId());
					}
		            String key = String.valueOf(System.currentTimeMillis());
		            history.put(key, BODY_PREFIX + messageBody);
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
	}
	
	
	private class StatusSendService extends TimerTask{
		
	  public void run(){
			
		   try{	
		   if (loggedIn()){
		      Message status = new Message();
		      status.setTo(ApplicationInstance.STATUS_APPID + "status" + ApplicationInstance.HOST_PREFIX);
		      status.setBody(ApplicationInstance.APPID  + ApplicationInstance.getInstance().getLoginName() + ApplicationInstance.HOST_PREFIX);
		      StreamXMPP.getInstance().sendPacket(status);
		    }
		  }catch(Throwable t){
			 Log.i("", "no connection send status"); 
		  }
	   }
		
	}
	
	private boolean loggedIn(){
		
		SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		if (settings != null) {
			userName = (String) settings.getString("username", "");
			password = (String) settings.getString("password", "");
		} else {
			userName = null;
			password = null;
		}
		
		if (userName != null && password != null && !userName.equals("") && !password.equals("")){
			return true;
		}
		return false;
		
	}
	
	private class ReconnectXMPPService extends TimerTask {
		
		public void run() {
			
			MessagingAckDB mackdb = ApplicationInstance.getInstance().getMessagingAckDB();
			if (mackdb == null){
				reintiDB();
			}
			
			boolean connected = true;
			long receivedStatusUpdateLastTime = ApplicationInstance.getInstance().getReceiveStatusUpdatedTime();
			long diff = (System.currentTimeMillis() - receivedStatusUpdateLastTime)/(60 * 1000);
			if (diff > 3){
				connected = false;
			}
			
			if (!StreamXMPP.getInstance().isConnected() || !connected){
			   if (loggedIn()){
				  boolean auth = StreamSession.authenticate(ApplicationInstance.APPID, ApplicationInstance.cKey,ApplicationInstance.sKey, getApplicationContext());
			      if (auth) {
					
					 try {
						StreamXMPP.getInstance().disconnect(); 
						StreamXMPP.getInstance().login(ApplicationInstance.APPID + userName, password);
						ApplicationInstance.getInstance().setReceiveStatusUpdatedTime(System.currentTimeMillis());
						ApplicationInstance.getInstance().setLoginName(userName);
						ApplicationInstance.getInstance().setPassword(password);
						ApplicationXMPPListener.getInstance().addListenerForAllUsers();
						ApplicationXMPPListener.getInstance().addFileReceiveListener();
			
					 } catch (XMPPException e) {
						Log.i("xmpp service", e.getMessage());
					 }
				  }
			   }
		   }
		}
	}

	private static int genereateId() {
        Random foo = new Random();
        int randomNumber = foo.nextInt(Integer.MAX_VALUE);
        return randomNumber;
    }

	@Override
	public void sendNotification(String expandedTitle, String expandedText,
			String message) {
		
		Intent intent = new Intent(this, MyFriendsActivity.class);
		intent.putExtra("nMessage", message);
		StreamSession.nMessage = message;
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification notification = new Notification (R.drawable.appicon1, message, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		notification.setLatestEventInfo(getApplicationContext(),
                expandedTitle,
                expandedText + " " + message,
                pIntent);
		int id = genereateId();
		
		mNotificationManager.notify(id, notification);
		ApplicationInstance.getInstance().addNotifacaionIds(String.valueOf(id));
		
	}
}
