package com.streamsdk.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

import com.stream.api.JsonUtils;
import com.stream.api.StreamFile;
import com.stream.xmpp.FileReceiveCallback;
import com.stream.xmpp.StreamXMPP;
import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.handler.AudioHandler;
import com.streamsdk.chat.handler.ImageHandler;

public class ApplicationXMPPListener {

	private static ApplicationXMPPListener applicationXMPPListener;
	
	public static ApplicationXMPPListener getInstance(){
		if (applicationXMPPListener == null){
			applicationXMPPListener = new ApplicationXMPPListener();
			return applicationXMPPListener;
		}
		return applicationXMPPListener;
	}
	
   public void addListenerForAllUsers() {
		
		StreamXMPP.getInstance().setPacketListenerForAll(new PacketListener() {
			public void processPacket(Packet packet) {
				ApplicationInstance.getInstance().setReceivedMessageLastTime(System.currentTimeMillis());
				Message message = (Message) packet;
				String jsonBody = message.getBody();
				StreamXMPPMessage xmppMessage = JsonUtils.parseNormalMessage(jsonBody);
				if (xmppMessage.getType().equals("ack")){
				   String ackId = xmppMessage.getAckId();
				   ApplicationInstance.getInstance().getMessagingAckDB().delete(ackId);
				   Log.i("ackid", ackId);
				   return;
				}
				StreamXMPP.getInstance().sendAck(ApplicationInstance.APPID + xmppMessage.getFrom(), xmppMessage.getId());
				if (xmppMessage.getType().equals("request")){
					String requestUserName = xmppMessage.getRequestUsername();
				    ApplicationInstance.getInstance().getFriendDB().insert(requestUserName, "request");
					return;
				}
				if (xmppMessage.getType().equals("friend")){
					String requestUserName = xmppMessage.getRequestUsername();
					ApplicationInstance.getInstance().getFriendDB().insert(requestUserName, "friend");
					return;
				}
				
				String parsed = EmojiParser.getInstance(ApplicationInstance.getInstance().getContext()).parseEmoji(xmppMessage.getMessage());
				IM im = new IM();
				im.setFrom(xmppMessage.getFrom());
				im.setTo(ApplicationInstance.getInstance().getLoginName());
				im.setChatMessage(parsed);
				im.setChatTime(System.currentTimeMillis());
				ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
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
			   }
			}
		});
		
	}
   
   public void addFileReceiveListener(){
		
		StreamXMPP.getInstance().addFileReceiveListener(new FileReceiveCallback() {
			 public void receiveFile(StreamFile streamFile, String body) {
					   ApplicationInstance.getInstance().setReceivedMessageLastTime(System.currentTimeMillis());
					   IM im =  new IM();
					   StreamXMPPMessage xmppMessage = JsonUtils.parseMediaMessaging(body);
					   String type = xmppMessage.getType();
					   boolean processed = true;
					   if (type.equals("photo")){
					     try {
							im = ImageHandler.processReceivedFriendImage(streamFile, true);
						} catch (Exception e) {
							processed = false;
						}
					   }else if (type.equals("video")){
						 try {
							im = ImageHandler.processReceivedFriendImage(streamFile, false);
						} catch (Exception e) {
							processed = false;
						}
					   }else{
						 String duration = xmppMessage.getDuration();
					     im = AudioHandler.processReceivedFile(streamFile, duration);
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
						   ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
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
					   }
					}
		}, null);
	}
	
}
