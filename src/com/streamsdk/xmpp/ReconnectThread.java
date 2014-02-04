package com.streamsdk.xmpp;

import org.jivesoftware.smack.XMPPException;

import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.RefreshUI;
import com.streamsdk.chat.handler.MessageHistoryHandler;

public class ReconnectThread implements Runnable{

	RefreshUI rui;
	
	public ReconnectThread(RefreshUI ru){
		rui = ru;
	}
	
	public void run() {
     
		try {
		
			 ApplicationInstance.getInstance().setCheckConnection(true);
			 StreamXMPP.getInstance().login(ApplicationInstance.APPID + ApplicationInstance.getInstance().getLoginName(), "111");
			 ApplicationXMPPListener.getInstance().addListenerForAllUsers();
			 ApplicationXMPPListener.getInstance().addFileReceiveListener();
			 new Thread(new MessageHistoryHandler(ApplicationInstance.getInstance().getLoginName(),ApplicationInstance.getInstance().getContext(), rui)).start();
	
		   } catch (XMPPException e) {
		
		   } catch (Throwable t){
			   
		   }
	}
	
}
