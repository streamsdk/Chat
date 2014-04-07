package com.streamsdk.chat.settings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class StatusListAdapter extends BaseAdapter{

    private Activity activity;
    private List<String> statuses;
    private CheckedTextView checkedView;
    
	public StatusListAdapter(Activity ac){
		StatusDB sdb = ApplicationInstance.getInstance().getStatusDB();
		this.statuses = sdb.getAllStatus();
		this.activity = ac;
	}
	
	public void setNewStatus(){
		StatusDB sdb = ApplicationInstance.getInstance().getStatusDB();
		this.statuses = sdb.getAllStatus();
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
