package com.streamsdk.chat.addfriend;

import java.util.List;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.streamsdk.chat.domain.FriendRequest;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendRequestListAdapter extends BaseAdapter{

	private Activity activity;
	private List<FriendRequest> requests;
	
	public FriendRequestListAdapter(Activity ac){
	      this.activity  = ac;
	      requests = ApplicationInstance.getInstance().getFriendDB().getFriendRequestList();
	}
	
	public int getCount() {
          return requests.size();
	}

	public Object getItem(int position) {
		return requests.get(position);
	}
	
	public void notifyListChanges(){
		requests  = ApplicationInstance.getInstance().getFriendDB().getFriendRequestList();
	}

	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup vg) {
	
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewHolder viewHolder;
		final FriendRequest friendRequest = (FriendRequest)getItem(position);
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.friendsrequest_layout, vg, false);
			viewHolder = new ViewHolder();
			viewHolder.imageAvatar = (ImageView)v.findViewById(R.id.imgAvatar);
			viewHolder.txtFriendName = (TextView)v.findViewById(R.id.txtFriendName);
			viewHolder.bFriendStatus = (Button)v.findViewById(R.id.acceptFriend);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		viewHolder.txtFriendName.setText(friendRequest.getFriendName());
		viewHolder.bFriendStatus.setText(friendRequest.getStatus());
		viewHolder.bFriendStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (friendRequest.getStatus().equals("request")){
					// update my friend status (from request->friend) in my category
					StreamObject myStreamCategoryObject = new StreamObject();
					myStreamCategoryObject.setToCategoriedObject(ApplicationInstance.getInstance().getLoginName());
					myStreamCategoryObject.setId(friendRequest.getFriendName());
					myStreamCategoryObject.put("status", "friend");
					myStreamCategoryObject.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
                             if (succeed){
                            	 if (!ApplicationInstance.getInstance().getFriendDB().userNameExists(friendRequest.getFriendName()))
                            	     ApplicationInstance.getInstance().getFriendDB().insert(friendRequest.getFriendName(), "friend");
                            	 else
                            		 ApplicationInstance.getInstance().getFriendDB().update(friendRequest.getFriendName(), "friend");
                             }
                         }
					});
					
					// update my friend status (from sendRequest->friend) in my category
					StreamObject myFriendCategoryObject = new StreamObject();
					myFriendCategoryObject.setToCategoriedObject(friendRequest.getFriendName());
					myFriendCategoryObject.setId(ApplicationInstance.getInstance().getLoginName());
					myFriendCategoryObject.put("status", "friend");
					myFriendCategoryObject.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
							if (succeed)
								Log.i("", "");
						}
					});
					
					viewHolder.bFriendStatus.setText("friend");
					
				}
				
				if (friendRequest.getStatus().equals("friend")){
					// update my friend status (from friend->request) in my category
					StreamObject myStreamCategoryObject = new StreamObject();
					myStreamCategoryObject.setToCategoriedObject(ApplicationInstance.getInstance().getLoginName());
					myStreamCategoryObject.setId(friendRequest.getFriendName());
					myStreamCategoryObject.put("status", "request");
					myStreamCategoryObject.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
                             if (succeed)
                            	 ApplicationInstance.getInstance().getFriendDB().update(friendRequest.getFriendName(), "request");
         				}
					});
					
					// update my friend status (from friend->sendRequest) in my category
					StreamObject myFriendCategoryObject = new StreamObject();
					myFriendCategoryObject.setToCategoriedObject(friendRequest.getFriendName());
					myFriendCategoryObject.setId(ApplicationInstance.getInstance().getLoginName());
					myFriendCategoryObject.put("status", "sendRequest");
					myFriendCategoryObject.updateObjectInBackground(new StreamCallback() {
						public void result(boolean succeed, String errorMessage) {
							if (succeed)
								Log.i("", "");
						}
					});
					
					viewHolder.bFriendStatus.setText("request");
					
				}
			}
		});
		
		v.setTag(viewHolder);
		return v;
	}

  static class ViewHolder{
	  
	  ImageView imageAvatar;
	  TextView txtFriendName;
	  Button bFriendStatus;
	  
  }

}



