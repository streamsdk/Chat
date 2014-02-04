package com.streamsdk.chat.addfriend;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;


public class SearchFriendFragment extends Fragment{

	private String userName = "";
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
					    if (succeed)
					    	ApplicationInstance.getInstance().getInivitationDB().insert(userName);
					}
				});
			}
		});
		
		Handler handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message message) {
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
