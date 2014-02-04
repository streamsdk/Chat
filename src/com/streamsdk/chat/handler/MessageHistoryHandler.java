package com.streamsdk.chat.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.stream.api.JsonUtils;
import com.stream.api.StreamFile;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPPMessage;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.RefreshUI;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.emoji.EmojiParser;

public class MessageHistoryHandler implements Runnable{
	
	private String userName = "";
	public static final String messageHistory = "messaginghistory";
	public static final String fixedPrefix = "message.body.";
	private static final int fixedLength = fixedPrefix.length();
	private Context context;
	private RefreshUI rfreshUI;
	
	public MessageHistoryHandler(String userName, Context context, RefreshUI rUi){
		this.userName = userName;
		this.context = context;
		this.rfreshUI = rUi;
	}
	
	public void run() {
		String id = userName + messageHistory;
		StreamObject history = new StreamObject();
		history.populateStreamObject(id);
		Map<String, Object> offlineStaff = history.getData();
		Set<Entry<String, Object>> entrySet = offlineStaff.entrySet();
		
		for (Entry<String, Object> data : entrySet){
			String timeKey = data.getKey();
			String jsonValue = (String)data.getValue();
			String json = jsonValue.substring(fixedLength);
			StreamXMPPMessage xmppMessage = JsonUtils.parseMediaMessaging(json);
			xmppMessage.setTime(timeKey);
			IM im = new IM();
			String type = xmppMessage.getType();
			StreamFile streamFile = new StreamFile();
			if (type.equals("photo")){
				  streamFile.setId(xmppMessage.getFileId());
				  try {
					  im = ImageHandler.processReceivedFriendImage(streamFile, true);
				  } catch (Exception e) {
					  continue;
				  }
			}else if (type.equals("video")){
				   streamFile.setId(xmppMessage.getFileId());
				   try {
					   im = ImageHandler.processReceivedFriendImage(streamFile, false);
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

			  im.setTo(userName);
			  im.setFrom(xmppMessage.getFrom());
		      im.setChatTime(Long.parseLong(timeKey));
		      
		      if (!xmppMessage.getTimeout().equals("") && (im.isVideo() || im.isImage())){
				   im.setTimeout(xmppMessage.getTimeout());
				   im.setDisappear(true);
				   im.setViewed("NO");
			   }
		      
		      try{
			      ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
			      ApplicationInstance.getInstance().getMessagingCountDB().insert(String.valueOf(im.getChatTime()), im.getFrom());
		      }catch(Throwable t){
		          Log.i("", t.getMessage());	  
		      }
		      
		}
		if (rfreshUI != null)
		     rfreshUI.refresh();
		Set<String> keys = offlineStaff.keySet();
		if (keys.size() > 0)
		     removeKeys(keys);
		
		
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
         so.setId(userName + messageHistory);
         so.removeKeyInBackground(keysRemoved);
  	 
	 }
	 
}
