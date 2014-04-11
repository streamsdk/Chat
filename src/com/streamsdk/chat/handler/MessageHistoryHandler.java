package com.streamsdk.chat.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

import com.stream.api.JsonUtils;
import com.stream.api.StreamFile;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.xmpp.NotificationInterface;

public class MessageHistoryHandler implements Runnable{
	
	private String userName = "";
	public static final String fixedPrefix = "message.body.";
	private static final int fixedLength = fixedPrefix.length();
	private Context context;
	private NotificationInterface notificationInterface;
	
	public MessageHistoryHandler(String userName, Context context){
		this.userName = userName;
		this.context = context;
	}
	
	public void run() {
		String id = userName + ApplicationInstance.messageHistory;
		StreamObject history = new StreamObject();
		history.populateStreamObject(id);
		Map<String, Object> offlineStaff = history.getData();
		Set<Entry<String, Object>> entrySet = offlineStaff.entrySet();
		
		boolean notificationSent = false;
		for (Entry<String, Object> data : entrySet){
			String timeKey = data.getKey();
			String jsonValue = (String)data.getValue();
			String json = jsonValue.substring(fixedLength);
			StreamXMPPMessage xmppMessage = JsonUtils.parseMediaMessaging(json);
			xmppMessage.setTime(timeKey);
			IM im = new IM();
			String type = xmppMessage.getType();
			StreamFile streamFile = new StreamFile();
			if (type.equals("") || type.equals("map")){
				continue;
			}
			if (type.equals("friend") || type.equals("request")){
				String requestUserName = xmppMessage.getRequestUsername();
			    try{	
				    ApplicationInstance.getInstance().getFriendDB().syncUpdate(requestUserName, type);
				}catch(Throwable t){}
			    continue;
			}
			if (type.equals("photo")){
				  streamFile.setId(xmppMessage.getFileId());
				  try {
					  im = ImageHandler.processReceivedFriendImage(streamFile, true, false);
				  } catch (Exception e) {
					  continue;
				  }
			}else if (type.equals("video")){
				   streamFile.setId(xmppMessage.getFileId());
				   try {
					   im = ImageHandler.processReceivedFriendImage(streamFile, false, false);
				   } catch (Exception e) {
                       continue;
				   }
		    }else if (type.equals("voice")){
				    streamFile.setId(xmppMessage.getFileId());
				    String duration = xmppMessage.getDuration();
			        im = AudioHandler.processReceivedFile(streamFile, duration);
			}else{
			        String parsed = EmojiParser.getInstance(context).parseEmoji(xmppMessage.getMessage());
				    im.setChatMessage(parsed);
			}
			
			if (!xmppMessage.getTimeout().equals("") && (type.equals("video") ||type.equals("photo"))){
				   im.setTimeout(xmppMessage.getTimeout());
				   im.setDisappear(true);
				   im.setViewed("NO");
			 }
			

			  im.setTo(userName);
			  im.setFrom(xmppMessage.getFrom());
		      im.setChatTime(Long.parseLong(timeKey));
		      
		      try{
			      ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
			      ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
		      }catch(Throwable t){
		         continue;
		      }
		      
		      if (!notificationSent && notificationInterface != null && !ApplicationInstance.getInstance().isVisiable()){
		    	  String notificationMessage = "";
		    	  if (im.isImage()){
		    		  notificationMessage = im.getFrom() + " sent a photo to you";
		    	  }else if (im.isVideo()){
		    		  notificationMessage = im.getFrom() + " sent a video to you";
		    	  }else if (im.isVoice()){
		    		  notificationMessage = im.getFrom() + " sent a voice message to you";
		    	  }else{
		    		  notificationMessage = im.getFrom() + ": " + im.getChatMessage(); 
		    	  }
		    	  notificationInterface.sendNotification("New Message", "", notificationMessage);
		    	  notificationSent = true;
		    	 //TODO: SENT NOTIFICATION 
		      }
		      
		}
		if (ApplicationInstance.getInstance().getRefreshUI() != null)
			ApplicationInstance.getInstance().getRefreshUI().refresh();
		Set<String> keys = offlineStaff.keySet();
		if (keys.size() > 0)
		     removeKeys(keys);
		
	 }
	
	 public NotificationInterface getNotificationInterface() {
		return notificationInterface;
	 }

	 public void setNotificationInterface(NotificationInterface notificationInterface) {
		this.notificationInterface = notificationInterface;
	 }

	private void removeKeys(Set<String> keys){
		 
		 String keysRemoved = "";
		 int index = 0;
         for (String key : keys){
        	 if (index != keys.size() - 1){
        		 keysRemoved = keysRemoved + key + "&&";
        	 }else{
        		 keysRemoved = keysRemoved + key;
        	 }
        	 index++;
         }
         StreamObject so = new StreamObject();
         so.setId(userName + ApplicationInstance.messageHistory);
         so.removeKeyInBackground(keysRemoved);
  	 
	 }
	 
}
