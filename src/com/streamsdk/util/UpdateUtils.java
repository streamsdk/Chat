package com.streamsdk.util;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.domain.OnlineOffineUpdate;

public class UpdateUtils {

	public static void updateOnline(String userName){
		
		final StreamObject so = new StreamObject();
		so.setId(userName + "status");
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
			    }
			}
		});
		
	}
	
	public static void updateOffline(String userName){
		
		final StreamObject so = new StreamObject();
		so.setId(userName + "status");
		so.put("online", "NO");
		so.updateObjectInBackground(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
			    if (succeed){
			    	OnlineOffineUpdate oou = new OnlineOffineUpdate();
			    	oou.setLastUpdatedTime(System.currentTimeMillis());
			    	oou.setUpdateToOffline(true);
			    	oou.setUpdateToOnline(false);
			    	ApplicationInstance.getInstance().setOnlineOfflineUpdate(oou);
			    }
			}
		});
		
	}
	
}
