package com.streamsdk.xmpp;

import android.util.Log;

import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.RefreshUI;

public class ConnectionCheck implements Runnable {
	
	private RefreshUI rui;
	public ConnectionCheck(RefreshUI ru){
		rui = ru;
	}
	
	public void run() {
		
		while (ApplicationInstance.getInstance().isCheckConnection()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {

			}
			long receivedLastTime = ApplicationInstance.getInstance().getReceivedMessageLastTime();
			long now = System.currentTimeMillis();
			if (receivedLastTime == 0)
				ApplicationInstance.getInstance().setReceivedMessageLastTime(now);
			long diff = (now - receivedLastTime) / 1000;
			Log.i("time check", String.valueOf(diff));
			if (diff > 600 && receivedLastTime != 0) {
				ApplicationInstance.getInstance().setReceivedMessageLastTime(now);
				if (StreamXMPP.getInstance().isConnected()) {
					Log.i("time check", "disconnect");
					StreamXMPP.getInstance().disconnect();
					//StreamXMPP.getInstance().disconnect();
					if (ApplicationInstance.getInstance().isVisiable()) {
						new Thread(new ReconnectThread(rui)).start();
					}
				}
			}
		}

		Log.i("time check", "out side disconnect");
		StreamXMPP.getInstance().disconnect();
	}
}
