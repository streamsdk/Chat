package com.streamsdk.chat.addfriend;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.streamsdk.chat.R;

public class HistoryAdapter extends BaseAdapter{

	private List<String> invitations;
	private Activity activity;
    public HistoryAdapter(List<String> invi, Activity ac){
    	this.invitations = invi;
    	this.activity = ac;
    }
	
	public int getCount() {
		return invitations.size();
	}

	public Object getItem(int position) {
		return invitations.get(position);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View view, ViewGroup vg) {
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String friendName = (String)getItem(position);
		ViewHolder viewHolder;
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.history_layout, vg, false);
			viewHolder = new ViewHolder();
		    viewHolder.imageView = (ImageView)v.findViewById(R.id.imgHistoryAvatar);
		    viewHolder.textView = (TextView)v.findViewById(R.id.txtHistoryFriendName);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		viewHolder.textView.setText(friendName);
		v.setTag(viewHolder);
		return v;
	}
	
	static class ViewHolder{
		
		ImageView imageView;
		TextView textView;
	}

}
