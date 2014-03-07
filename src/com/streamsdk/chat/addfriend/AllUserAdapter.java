package com.streamsdk.chat.addfriend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class AllUserAdapter extends BaseAdapter{

	private List<Map<String, String>> users;
	private Activity activity;
	
	
	public AllUserAdapter(Activity ac){
		 users = new ArrayList<Map<String, String>>();
		 List<Map<String, String>> tempUsers = ApplicationInstance.getInstance().getAllUsers();
	     activity = ac;
	     List<String> history = ApplicationInstance.getInstance().getInivitationDB().getInvitations();
	     List<String> names = ApplicationInstance.getInstance().getFriendDB().getFriends();
	     for (Map<String, String> user : tempUsers){
	    	 String nId = user.get("name");
	    	 if (!history.contains(nId) && !names.contains(nId)){
	    		users.add(user);
	    	 }
	     }
	     
	}
	
	private void removeUser(String name){
		for (int i=0; i<users.size(); i++){
			Map<String, String> user = users.get(i);
			String n = user.get("name");
			if (name.equals(n)){
				users.remove(i);
				break;
			}
		}
	}
	
	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Map<String, String> getItem(int location) {
		return users.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

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
	  
	@Override
	public View getView(int position, View view, ViewGroup vg) {
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewHolder viewHolder;
		final Map<String, String> user = (Map<String, String>)getItem(position);
		final String userName = user.get("name");
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.allfriends_layout, vg, false);
			viewHolder = new ViewHolder();
			viewHolder.allImageAvatar = (ImageView)v.findViewById(R.id.allImgAvatar);
			viewHolder.allTxtFriendName = (TextView)v.findViewById(R.id.allTxtFriendName);
			viewHolder.allBFriendStatus = (ImageView)v.findViewById(R.id.allAcceptFriend);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		Bitmap bitmap = ImageCache.getInstance().getImage(userName);
		if (bitmap != null)
		    viewHolder.allImageAvatar.setImageBitmap(bitmap);
		else{
			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.yahoo_no_avatar);
			viewHolder.allImageAvatar.setImageBitmap(bm);
		}
		viewHolder.allTxtFriendName.setText(userName);
		viewHolder.allBFriendStatus.setImageResource(R.drawable.addfriend);
		v.setTag(viewHolder);
		
		viewHolder.allBFriendStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		        alertDialogBuilder
				.setMessage("Would you like to send a friend request to " + userName + " ?")
				.setCancelable(false)
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								 removeUser(userName);
								 notifyDataSetChanged();
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
				}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
				});
				
		        AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
		
		
		return v;
	}
	
	

	
	
	static class ViewHolder{
		  
		  ImageView allImageAvatar;
		  TextView allTxtFriendName;
		  ImageView allBFriendStatus;
		  
	  }

}
