package com.streamsdk.chat.addfriend;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;


public class SearchFriendFragment extends Fragment{

	private String userName = "";
	
    private String buildFriendRequest(){
    	
    	JSONObject friendReuqest = new JSONObject();
    	try {
			friendReuqest.put("type", "request");
		    friendReuqest.put("username", ApplicationInstance.getInstance().getLoginName());
		    friendReuqest.put("id", String.valueOf(System.currentTimeMillis()));
	        return friendReuqest.toString();
	        
    	} catch (JSONException e) {
			
		}
    	
    	
    	return "";
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.searchfriend_layout, container, false);
		final ImageView iv = (ImageView)v.findViewById(R.id.imgSearchAvatar);
		final TextView tv = (TextView)v.findViewById(R.id.txtSearchFriendName);
		final Button requestButton  = (Button)v.findViewById(R.id.sendRequestFriend);
		requestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			     StreamObject requestUser = new StreamObject();
			     requestUser.setId(ApplicationInstance.getInstance().getLoginName());
				 requestUser.setToCategoriedObject(userName);
				 requestUser.put("status", "request");
				 requestUser.updateObjectInBackground(new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					    if (succeed){
					    	ApplicationInstance.getInstance().getInivitationDB().insert(userName);
					        String friendRequest = buildFriendRequest();
					        Message packet = new Message();
					        String to = ApplicationInstance.APPID + userName + ApplicationInstance.HOST_PREFIX;
					        Log.i("request to", to);
					        packet.setTo(to);
					        packet.setBody(friendRequest);
					        StreamXMPP.getInstance().sendPacket(packet);
					    }
					}
				});
			}
		});
		
		Handler handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(android.os.Message message) {
				Bundle bundle = message.getData();
				userName = (String)bundle.getString("userName");				
				iv.setVisibility(View.VISIBLE);
				tv.setText(userName);
				tv.setVisibility(View.VISIBLE);
				requestButton.setVisibility(View.VISIBLE);
				return true;
			}
		});
		ApplicationInstance.getInstance().addHandler("search", handler);
		
	
		return v;
	}
	
}
