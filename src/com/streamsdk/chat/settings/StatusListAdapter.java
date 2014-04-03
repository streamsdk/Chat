package com.streamsdk.chat.settings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class StatusListAdapter extends BaseAdapter{

    private List<String> statuses;	
    private Activity activity;
    private CheckedTextView checkedView;
    
	public StatusListAdapter(List<String> statuses, Activity ac){
		this.statuses = statuses;
		this.activity = ac;
	}
	
	@Override
	public int getCount() {
		return statuses.size();
	}

	@Override
	public Object getItem(int position) {
		return statuses.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, final View view, ViewGroup vg)  {
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String status = (String)getItem(position);
		ViewHolder viewHolder;
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.statuslistitem_layout, vg, false);
			viewHolder = new ViewHolder();
			viewHolder.checkedTextView = (CheckedTextView)v.findViewById(R.id.myCurrentStatus);
			viewHolder.checkedTextView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					 if (checkedView != null)
						 checkedView.setChecked(false);
					 CheckedTextView ctv = (CheckedTextView)v;
					 checkedView = ctv;
				     ctv.setChecked(true);
				     ApplicationInstance.getInstance().setCurrentStatus(ctv.getText().toString());
				     notifyDataSetChanged();
				}
			});
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.checkedTextView.setText(status);
		if (status.equals(ApplicationInstance.getInstance().getCurrentStatus())){
			viewHolder.checkedTextView.setChecked(true);
		}else{
			viewHolder.checkedTextView.setChecked(false);
		}
		v.setTag(viewHolder);
		return v;
	}
	
	static class ViewHolder{
		CheckedTextView checkedTextView;
	}
}
