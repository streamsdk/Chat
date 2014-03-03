package com.streamsdk.xmpp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

import com.stream.api.JsonUtils;
import com.stream.api.StreamFile;
import com.stream.xmpp.FileReceiveCallback;
import com.stream.xmpp.StreamXMPP;
import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.handler.AudioHandler;
import com.streamsdk.chat.handler.ImageHandler;

public class ApplicationXMPPListener {

	private static ApplicationXMPPListener applicationXMPPListener;
	private Map<String, NotificationInterface> nListeners = new HashMap<String, NotificationInterface>();
	
	public static ApplicationXMPPListener getInstance(){
		if (applicationXMPPListener == null){
			applicationXMPPListener = new ApplicationXMPPListener();
			return applicationXMPPListener;
		}
		return applicationXMPPListener;
   }
	
   public void addNotifier(String name, NotificationInterface ni){
	   nListeners.put(name, ni);
   }
   
   public void sendNotifications(String expandedTitle, String expandedText, String message){
	   if (!ApplicationInstance.getInstance().isVisiable()){
		   Set<String> listeners = nListeners.keySet();
		   Log.i("ready to send notification message", message);
		   for (String lis : listeners){
			   NotificationInterface ni = nListeners.get(lis);
			   Log.i("send notification message", message);
			   ni.sendNotification(expandedTitle, expandedText, message);
		   }
	   }
   }
  
    public void addListenerForAllUsers() {
		
		StreamXMPP.getInstance().setPacketListenerForAll(new PacketListener() {
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
				if (message.getBody().equals(ApplicationInstance.APPID + ApplicationInstance.getInstance().getLoginName() + ApplicationInstance.HOST_PREFIX)){
					Log.i("returned", "returned");
					ApplicationInstance.getInstance().setReceiveStatusUpdatedTime(System.currentTimeMillis());
					return;
				}
				String jsonBody = message.getBody();
				StreamXMPPMessage xmppMessage = JsonUtils.parseNormalMessage(jsonBody);
				if (xmppMessage.getType().equals("ack")){
				   String ackId = xmppMessage.getAckId();
				   ApplicationInstance.getInstance().getMessagingAckDB().delete(ackId);
				   Log.i("ackid", ackId);
				   return;
				}
				
				if (xmppMessage.getType().equals("request")){
					String requestUserName = xmppMessage.getRequestUsername();
				    ApplicationInstance.getInstance().getFriendDB().syncUpdate(requestUserName, "request");
					return;
				}
				if (xmppMessage.getType().equals("friend")){
					String requestUserName = xmppMessage.getRequestUsername();
					ApplicationInstance.getInstance().getFriendDB().syncUpdate(requestUserName, "friend");
					return;
				}
				
				if (xmppMessage.getType().equals("sendRequest")){
					return;
				}
				
				StreamXMPP.getInstance().sendAck(ApplicationInstance.APPID + xmppMessage.getFrom(), xmppMessage.getId());
				String parsed = EmojiParser.getInstance(ApplicationInstance.getInstance().getContext()).parseEmoji(xmppMessage.getMessage());
				IM im = new IM();
				im.setPrimaryKey(xmppMessage.getId());
				im.setFrom(xmppMessage.getFrom());
				im.setTo(ApplicationInstance.getInstance().getLoginName());
				im.setChatMessage(parsed);
				im.setChatTime(System.currentTimeMillis());
				
				try{
				  ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
				}catch(Throwable t){
				  Log.i("received double messaging", "return");
				  return;
				}
				  
				if (ApplicationInstance.getInstance().getCurrentChatListener() != null){
				    String receiver = ApplicationInstance.getInstance().getCurrentChatListener().getReceiver();
				    if (receiver.equals(im.getFrom()))
				        ApplicationInstance.getInstance().getCurrentChatListener().receiveMessage(im);
				    else{
						ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
						if (ApplicationInstance.getInstance().getRefreshUI() != null){
							ApplicationInstance.getInstance().getRefreshUI().refresh();
						}					    	
				    }
				}else{
					ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
					if (ApplicationInstance.getInstance().getRefreshUI() != null){
						ApplicationInstance.getInstance().getRefreshUI().refresh();
					}
					ImageCache.getInstance().removeAll();
					sendNotifications("New Message", "", im.getFrom() + ": " + xmppMessage.getMessage());
			   }
			}
		});
		
	}
   
   public void addFileReceiveListener(){
		
		StreamXMPP.getInstance().addFileReceiveListener(new FileReceiveCallback() {
			 public void receiveFile(StreamFile streamFile, String body) {
					   IM im =  new IM();
					   StreamXMPPMessage xmppMessage = JsonUtils.parseMediaMessaging(body);
					   String type = xmppMessage.getType();
					   String notificationMessage = "";
					   boolean processed = true;
					   if (type.equals("photo")){
					     try {
							im = ImageHandler.processReceivedFriendImage(streamFile, true);
							notificationMessage = " sent a photo to you";
						} catch (Exception e) {
							processed = false;
						}
					   }else if (type.equals("video")){
						 try {
							im = ImageHandler.processReceivedFriendImage(streamFile, false);
							notificationMessage = " sent a video to you";
						} catch (Exception e) {
							processed = false;
						}
					   }else{
						 String duration = xmppMessage.getDuration();
					     im = AudioHandler.processReceivedFile(streamFile, duration);
					     notificationMessage = " sent a voice message to you";
					   }
					   if (!xmppMessage.getTimeout().equals("") && ((type.equals("video") || type.equals("photo")))){
						   im.setTimeout(xmppMessage.getTimeout());
						   im.setDisappear(true);
						   im.setViewed("NO");
					   }
					   im.setFrom(xmppMessage.getFrom());
					   im.setTo(ApplicationInstance.getInstance().getLoginName());
					   im.setChatTime(System.currentTimeMillis());
					   if (processed){
						   im.setPrimaryKey(xmppMessage.getId());
						   try{
						     ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
						   }catch(Throwable t){
							 Log.i("received double file", "return");
							 return;
						   }
						   StreamXMPP.getInstance().sendAck(ApplicationInstance.APPID + xmppMessage.getFrom(), xmppMessage.getId());
					   }
					   if (ApplicationInstance.getInstance().getCurrentChatListener() != null && processed){
						    String receiver = ApplicationInstance.getInstance().getCurrentChatListener().getReceiver();
						    if (receiver.equals(im.getFrom()))
						        ApplicationInstance.getInstance().getCurrentChatListener().receiveMessage(im);
						    else{
								ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
								 if (ApplicationInstance.getInstance().getRefreshUI() != null){
										ApplicationInstance.getInstance().getRefreshUI().refresh();
								 }			    	
						    }
						}else{
							ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
							if (ApplicationInstance.getInstance().getRefreshUI() != null){
								ApplicationInstance.getInstance().getRefreshUI().refresh();
							}
							ImageCache.getInstance().removeAll();
							sendNotifications("New Message", "", im.getFrom() + notificationMessage);
					   }
					}
		}, null);
	}
	
}
