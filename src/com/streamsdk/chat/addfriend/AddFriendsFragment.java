package com.streamsdk.chat.addfriend;

import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import com.streamsdk.chat.domain.FriendRequest;


public class AddFriendsFragment extends ListFragment{


	PopupWindow popupWindow;
	View popupView;
	String userName;
	
     private String buildFriendRequest(String friendName){
	    	
	    	JSONObject friendReuqest = new JSONObject();
	    	try {
				friendReuqest.put("type", "request");
			    friendReuqest.put("username", ApplicationInstance.getInstance().getLoginName());
			    friendReuqest.put("friendname", friendName);
			    friendReuqest.put("id", String.valueOf(System.currentTimeMillis()));
		        return friendReuqest.toString();
		        
	    	} catch (JSONException e) {
				
			}
	    	
	    	
	    	return "";
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final List<FriendRequest> requests = ApplicationInstance.getInstance().getFriendDB().getFriendRequestList();
		final FriendRequestListAdapter fAdapter = new FriendRequestListAdapter(this.getActivity());		
		final View v = inflater.inflate(R.layout.friendlist_layout, container, false);
		
		popupView = getActivity().getLayoutInflater().inflate(R.layout.searchresults_layout, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
		final TextView txtView = (TextView)popupView.findViewById(R.id.txtAddAsFriend);
		
		Handler handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(android.os.Message message) {
				List<FriendRequest> newRequests = ApplicationInstance.getInstance().getFriendDB().getFriendRequestList();
				if (newRequests!= null && newRequests.size() > 0){
					fAdapter.notifyListChanges();
					setListAdapter(fAdapter);						
					TextView tv = (TextView)v.findViewById(R.id.txtNoRquest);
					tv.setVisibility(View.GONE);
					updateData(fAdapter);					
				 }
				return true;
			}
		});
		
		Handler searchHandler = new Handler(new Handler.Callback(){
			public boolean handleMessage(android.os.Message message) {
				Bundle bundle = message.getData();
				userName = (String)bundle.getString("userName");
				txtView.setText("Do you want to add "  +  userName + " as your friend?");
				popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				return true;
 			}
		});
		
		 Button buttonOK = (Button)popupView.findViewById(R.id.searchOptionsButtonOK);
	     buttonOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					 popupWindow.dismiss();
					 StreamObject requestUser = new StreamObject();
				     requestUser.setId(ApplicationInstance.getInstance().getLoginName());
					 requestUser.setToCategoriedObject(userName);
					 requestUser.put("status", "request");
					 requestUser.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
						    if (succeed){
						    	ApplicationInstance.getInstance().getInivitationDB().insert(userName);
						        String friendRequest = buildFriendRequest(userName);
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
	       
	     Button buttonCancel = (Button)popupView.findViewById(R.id.searchButtonCancel);
	     buttonCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				     popupWindow.dismiss();	
				}
		 });
		
		
		ApplicationInstance.getInstance().addHandler("add", handler);
		ApplicationInstance.getInstance().addHandler("search", searchHandler);
		
		if (requests != null && requests.size()  > 0){
			setListAdapter(fAdapter);
			updateData(fAdapter);
		}else{
			TextView tv = (TextView)v.findViewById(R.id.txtNoRquest);
			tv.setVisibility(View.VISIBLE);
		}
		
		return v;
	}
	
	public void dismissPopup(){
		if (popupWindow != null){
			popupWindow.dismiss();
		}
	}
	
     private void updateData(final BaseAdapter adapter){
    	Activity activity = getActivity();
    	if (activity != null){
    		activity.runOnUiThread(new Runnable(){
			    public void run() {
				   adapter.notifyDataSetChanged();
			    }
		    });
    	}
	}
	
}
