package com.streamsdk.chat.addfriend;

import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import com.streamsdk.chat.domain.FriendRequest;

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

	
	 private String buildFriend(String friendName){
	    	
		JSONObject friendReuqest = new JSONObject();
		try {
			friendReuqest.put("type", "friend");
			friendReuqest.put("username", ApplicationInstance.getInstance().getLoginName());
			friendReuqest.put("friendname", friendName);
			friendReuqest.put("id", String.valueOf(System.currentTimeMillis()));
			return friendReuqest.toString();

		} catch (JSONException e) {

		}
	    	return "";
	}

    public View getView(final int position, View view, ViewGroup vg) {
	
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewHolder viewHolder;
		final FriendRequest friendRequest = (FriendRequest)getItem(position);
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.friendsrequest_layout, vg, false);
			viewHolder = new ViewHolder();
			viewHolder.imageAvatar = (ImageView)v.findViewById(R.id.imgAvatar);
			viewHolder.txtFriendName = (TextView)v.findViewById(R.id.txtFriendName);
			viewHolder.bFriendStatus = (ImageView)v.findViewById(R.id.acceptFriend);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		Bitmap bitmap = ImageCache.getInstance().getImage(friendRequest.getFriendName());
		if (bitmap != null)
		    viewHolder.imageAvatar.setImageBitmap(bitmap);
		else{
			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.yahoo_no_avatar);
			viewHolder.imageAvatar.setImageBitmap(bm);
		}
		viewHolder.txtFriendName.setText(friendRequest.getFriendName());
		if (friendRequest.getStatus().equals("friend")){
		   viewHolder.bFriendStatus.setImageResource(R.drawable.friends);	
		}
		if (friendRequest.getStatus().equals("request")){
		   viewHolder.bFriendStatus.setImageResource(R.drawable.addfriend);
		}
		viewHolder.bFriendStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (friendRequest.getStatus().equals("request")){
					    String message = "add " + friendRequest.getFriendName() + " as your friend?";
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
				        alertDialogBuilder
						.setMessage(message)
						.setCancelable(false)
						.setPositiveButton("YES", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										// update my friend status (from request->friend) in my category
										((FriendRequest)getItem(position)).setStatus("friend");
										StreamObject myStreamCategoryObject = new StreamObject();
										myStreamCategoryObject.setToCategoriedObject(ApplicationInstance.getInstance().getLoginName());
										myStreamCategoryObject.setId(friendRequest.getFriendName());
										myStreamCategoryObject.put("status", "friend");
										myStreamCategoryObject.updateObjectInBackground(new StreamCallback() {
											public void result(boolean succeed, String errorMessage) {
					                             if (succeed){
					                            	 if (!ApplicationInstance.getInstance().getFriendDB().userNameExists(friendRequest.getFriendName())){
					                            	     ApplicationInstance.getInstance().getFriendDB().insert(friendRequest.getFriendName(), "friend");
					                            	 }else{
					                            		 ApplicationInstance.getInstance().getFriendDB().update(friendRequest.getFriendName(), "friend");
					                            	 }
					                            	 
					                            	String friendBody = buildFriend(friendRequest.getFriendName());
					     					        Message packet = new Message();
					     					        String to = ApplicationInstance.APPID + friendRequest.getFriendName() + ApplicationInstance.HOST_PREFIX;
					     					        Log.i("request to", to);
					     					        packet.setTo(to);
					     					        packet.setBody(friendBody);
					     					        StreamXMPP.getInstance().sendPacket(packet);
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
										
										viewHolder.bFriendStatus.setImageResource(R.drawable.friends);
										notifyDataSetChanged();
									}
						})
						.setNegativeButton("NO",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										dialog.cancel();
									}
						});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
				}
				
				
				
				if (friendRequest.getStatus().equals("friend")){
					
					String message = "remove " + friendRequest.getFriendName() + " as your friend?";
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
				    alertDialogBuilder
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("YES", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int which) {
								// update my friend status (from friend->request) in my category
								 ((FriendRequest)getItem(position)).setStatus("request");
								StreamObject myStreamCategoryObject = new StreamObject();
								myStreamCategoryObject.setToCategoriedObject(ApplicationInstance.getInstance().getLoginName());
								myStreamCategoryObject.setId(friendRequest.getFriendName());
								myStreamCategoryObject.put("status", "request");
								myStreamCategoryObject.updateObjectInBackground(new StreamCallback() {
									public void result(boolean succeed, String errorMessage) {
			                             if (succeed){
			                            	 ApplicationInstance.getInstance().getFriendDB().update(friendRequest.getFriendName(), "request");
			                             }
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
								
								viewHolder.bFriendStatus.setImageResource(R.drawable.addfriend);
								notifyDataSetChanged();					 	
						  }
					})
					.setNegativeButton("NO",new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
						  }
					});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			}
		});
		
		v.setTag(viewHolder);
		return v;
	}

  static class ViewHolder{
	  
	  ImageView imageAvatar;
	  TextView txtFriendName;
	  ImageView bFriendStatus;
	  
  }

}



