package com.streamsdk.chat.settings;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.stream.api.StreamUser;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class StatusSettingActivity extends ListActivity implements NewStatusListener{
	
	public static final int CHECKBOXTAG = 555566;
	LinearLayout statusLayout;
	String status;
	StatusListAdapter sla;
	
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
		 status = ApplicationInstance.getInstance().getCurrentStatus();
		 setContentView(R.layout.statuslist_layout);
		 sla = new StatusListAdapter(this);
		 setListAdapter(sla);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();
		if (title.equals("addstatus")){
			FragmentManager fm = getFragmentManager();
			StatusSettingDialog settings = new StatusSettingDialog(this);
			settings.show(fm, "newstatussettings");
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			 onBackPressed();
			 updateStatusIfNeeded();
			 return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
			 menu.add("addstatus").setIcon(R.drawable.addfri).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			 return true;
	}
	
	private void updateStatusIfNeeded(){
		if (!status.equals(ApplicationInstance.getInstance().getCurrentStatus())){
		  	 StreamUser su = new StreamUser();
			 su.updateUserMetadata("status", ApplicationInstance.getInstance().getCurrentStatus());
	    	 su.updateUserMetadataInBackground(ApplicationInstance.getInstance().getLoginName());
	    	 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		     SharedPreferences.Editor editor = settings.edit();
		     editor.putString("status", ApplicationInstance.getInstance().getCurrentStatus());
		     editor.commit();
		}
	}

	public void updateResult() {
		runOnUiThread(new Runnable(){
			public void run() {
				sla.setNewStatus();
				sla.notifyDataSetChanged();
			}
		});
	}
	

}
