package com.streamsdk.chat.group;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.stream.api.StreamObject;
import com.streamsdk.chat.R;

public class GroupThreadScreen extends ListActivity implements OnItemLongClickListener{
	
	GroupThreadAdapter gta;
	HoldFullScreenDialog hu;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupscreen_layout);
		
		gta = new GroupThreadAdapter(this);
		setListAdapter(gta);
		getListView().setOnItemLongClickListener(this);
		getListView().setOnKeyListener(new OnKeyListener() {
		
			public boolean onKey(View arg0, int arg1, KeyEvent ke) {
			    
				if (ke.getAction() == KeyEvent.ACTION_UP){
					Log.i("", "");
					hu.dismiss();
					return true;
				}
				
				return false;
			}
		});
		
	}

	public boolean onItemLongClick(AdapterView<?> av, View view, int index, long itemId) {
	   
	    StreamObject item = (StreamObject)gta.getItem(index);
	    hu = HoldFullScreenDialog.newInstance();
	    hu.show(getFragmentManager(), "");
	
		return true;
	}
	
}
