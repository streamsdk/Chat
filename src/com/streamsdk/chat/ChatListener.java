package com.streamsdk.chat;

import com.streamsdk.chat.domain.IM;

public interface ChatListener {

	public void receiveMessage(IM im);
	
	public void receiveFile(IM im);
	
	public String getReceiver();
	
}
