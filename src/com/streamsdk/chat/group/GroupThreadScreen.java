package com.streamsdk.chat.group;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.stream.api.StreamObject;
import com.streamsdk.chat.R;

public class GroupThreadScreen extends ListActivity implements OnItemLongClickListener,OnTouchListener{
	
	GroupThreadAdapter gta;
	HoldFullScreenDialog hu;
	Handler mHandler;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupscreen_layout);
		gta = new GroupThreadAdapter(this);
		setListAdapter(gta);
		getListView().setOnItemLongClickListener(this);
		getListView().setOnTouchListener(this);
		
	}
	
	public void updateAdapter(){
		runOnUiThread(new Runnable(){
			public void run() {
			  gta.notifyDataSetChanged();
			}
		});
	}

	public boolean onItemLongClick(AdapterView<?> av, View view, int index, long itemId) {
	    StreamObject item = (StreamObject)gta.getItem(index);
	    hu = HoldFullScreenDialog.newInstance();
	    hu.show(getFragmentManager(), "");
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    hu.startDownloading((String)item.get("fileId"), (String)item.get("length"), metrics);
	    return true;
	}

	public boolean onTouch(View v, MotionEvent event) {
	        boolean isReleased = event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;
	        boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;
	        if (isReleased) {
	           if (hu!=null)
	               hu.dismiss();
	        } else if (isPressed) {
	          
	        }
	        return false;
	}
}
