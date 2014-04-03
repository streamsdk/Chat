package com.streamsdk.chat.settings;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class StatusSettingActivity extends ListActivity{
	
	public static final int CHECKBOXTAG = 555566;
	LinearLayout statusLayout;
	@Override
    public void onPause(){
		super.onPause();
		ApplicationInstance.getInstance().setVisiable(false);
    }
	
	protected void onResume(){
		super.onResume();
		ApplicationInstance.getInstance().setVisiable(true);		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.statuslist_layout);
		 StatusDB sdb = ApplicationInstance.getInstance().getStatusDB();
		 List<String> statuses = sdb.getAllStatus();
		 StatusListAdapter sla = new StatusListAdapter(statuses, this);
		 setListAdapter(sla);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 onBackPressed();
			 return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
	}
	

}
