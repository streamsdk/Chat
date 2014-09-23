package com.streamsdk.chat;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.streamsdk.chat.addfriend.AddFriendMainActivity;
import com.streamsdk.chat.group.FullScreenTextDrawing;
import com.streamsdk.chat.group.GroupThreadScreen;
import com.streamsdk.chat.settings.PreferenceScreen;

public class CoolChatMainActivity extends TabActivity {
	
	Activity activity;
	TabHost tabHost;
	
	public void onCreate(Bundle savedInstanceState) {
	  
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_tab_host);
	    activity = this;
	    tabHost = getTabHost(); // The activity TabHost
	    TabSpec u = tabHost.newTabSpec("Friends");
	    u.setIndicator("Friends");
	    Intent updatesIntent = new Intent(this, MyFriendsActivity.class);
	    u.setContent(updatesIntent);

	    TabSpec m = tabHost.newTabSpec("Demo");
	    m.setIndicator("Demo");
	    Intent demo = new Intent(this, GroupThreadScreen.class);
	    m.setContent(demo);
	  
	    tabHost.addTab(u);
	    tabHost.addTab(m);
	    
	    
	    tabHost.setCurrentTab(0);
	    
	    tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
			    invalidateOptionsMenu();
			}
		});
	  
	  
	 }
	
	public boolean onOptionsItemSelected(MenuItem item) {

		String title = (String) item.getTitle();
		if (title.equals("Refresh")) {
			Intent intent = new Intent(activity, AddFriendMainActivity.class);
			startActivity(intent);
		}
		if (title.equals("Settings")) {
			Intent intent = new Intent(activity, PreferenceScreen.class);
			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		}
		if (title.equals("GroupPhoto")) {
			Intent intent = new Intent(activity, AndroidPhotoCapture.class);
			intent.putExtra("from", "group");
			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		}
		if (title.equals("GroupText")){
			Intent intent = new Intent(activity, FullScreenTextDrawing.class);
			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		}

		return super.onOptionsItemSelected(item);

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		 case  ApplicationInstance.FINISH_ALL:
			  String tabTag = getTabHost().getCurrentTabTag(); 
	    	  Activity activity = getLocalActivityManager().getActivity(tabTag); 
	    	  if (activity instanceof GroupThreadScreen){
	    		 GroupThreadScreen gts = (GroupThreadScreen)activity;
	    		 gts.updateAdapter();
	    	  } 
			 if(resultCode == RESULT_OK){        		    	  
		    	  finish();
		       }
		       break; 
		}
	}
	
  
    public boolean onPrepareOptionsMenu(Menu menu) {
           
    	   MenuInflater inflater = getMenuInflater();
           int currentTab = tabHost.getCurrentTab();
           menu.clear();
           if (currentTab == 0) {
               inflater.inflate(R.menu.frineds_menu, menu); 
           } else {
               inflater.inflate(R.menu.group_menu, menu); 
           }
         
           return super.onPrepareOptionsMenu(menu);
       }
    

}
