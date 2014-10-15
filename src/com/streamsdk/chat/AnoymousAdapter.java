package com.streamsdk.chat;

import java.util.List;
import java.util.Map;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.settings.UserDetailsViewActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AnoymousAdapter extends BaseAdapter{

    List<String> users;
    Activity activity;
	public AnoymousAdapter(List<String> u, Activity a){
    	this.users = u;
    	this.activity = a;
    }
	
	public int getCount() {
		return users.size();
	}

	public Object getItem(int position) {
		return users.get(position);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final String friendName = (String)getItem(position);
		ViewHolder viewHolder;
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.anonymous_layout, parent, false);
			viewHolder = new ViewHolder();
		    viewHolder.textView = (TextView)v.findViewById(R.id.txtAnoName);
		    viewHolder.statusTextView = (TextView)v.findViewById(R.id.txtAnoStatus);
		    viewHolder.imageView = (ImageView)v.findViewById(R.id.imgAnoMainPageAvatar);
		    viewHolder.messagingCountLayout = (FrameLayout)v.findViewById(R.id.messageAnoCountLayout);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		// friend name text
		viewHolder.textView.setText(friendName);
		
		
		// friend status
		Map<String, String> metaData = ApplicationInstance.getInstance().getFriendMetadata(friendName);
		if (metaData != null){
			String status = metaData.get("status");
			if (status != null && !status.equals("")){
				Log.i(friendName + " status", status);
				viewHolder.statusTextView.setText(status);
			 }else{
				viewHolder.statusTextView.setText("Hey there! I am using CoolChat.");
			 }
		 }
				
		//friend profile image
		Bitmap bitmap = ImageCache.getInstance().getFriendImage(friendName);
		if (bitmap != null)
			viewHolder.imageView.setImageBitmap(bitmap);
		else{
			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.yahoo_no_avatar);
			viewHolder.imageView.setImageBitmap(bm);
		}
		
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity.getApplicationContext(), UserDetailsViewActivity.class);
				intent.putExtra("username", friendName);
				activity.startActivity(intent);	
			}
		});
		
		//friend message count
		Map<String, String> mCounts = ApplicationInstance.getInstance().getMessagingCountDB().getMessagingCount(friendName);
		if (mCounts != null){
			viewHolder.messagingCountLayout.setVisibility(View.VISIBLE);
			TextView mCountView = (TextView)viewHolder.messagingCountLayout.findViewById(R.id.txtAnoMessageCount);
			mCountView.setText(String.valueOf(mCounts.size()));
		}else{
			viewHolder.messagingCountLayout.setVisibility(View.GONE);
		}
		
		v.setTag(viewHolder);
		return v;
	}
	
	 static class ViewHolder{
			TextView textView;
			TextView statusTextView;
			ImageView imageView;
			FrameLayout messagingCountLayout;
	 }

}
