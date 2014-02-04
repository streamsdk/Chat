package com.streamsdk.chat.addfriend;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import com.streamsdk.chat.domain.FriendRequest;


public class AddFriendsFragment extends ListFragment{


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final List<FriendRequest> requests = ApplicationInstance.getInstance().getFriendDB().getFriendRequestList();
		final FriendRequestListAdapter fAdapter = new FriendRequestListAdapter(this.getActivity());		
		final View v = inflater.inflate(R.layout.friendlist_layout, container, false);
		Handler handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message message) {
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
		
		ApplicationInstance.getInstance().addHandler("add", handler);
		
		if (requests != null && requests.size()  > 0){
			setListAdapter(fAdapter);
			updateData(fAdapter);
		}else{
			TextView tv = (TextView)v.findViewById(R.id.txtNoRquest);
			tv.setVisibility(View.VISIBLE);
		}
		
		return v;
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
