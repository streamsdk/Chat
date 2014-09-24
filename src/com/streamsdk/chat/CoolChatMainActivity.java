package com.streamsdk.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.stream.api.StreamCallback;
import com.stream.api.StreamCategoryObject;
import com.streamsdk.chat.addfriend.AddFriendMainActivity;
import com.streamsdk.chat.group.FullScreenImageDrawing;
import com.streamsdk.chat.group.FullScreenTextDrawing;
import com.streamsdk.chat.group.GroupThreadScreen;
import com.streamsdk.chat.handler.ImageHandler;
import com.streamsdk.chat.settings.PreferenceScreen;

public class CoolChatMainActivity extends TabActivity {
	
	Activity activity;
	TabHost tabHost;
	static final int REQUEST_IMAGE_PICK = 1;
	static long lastRunTime = 0;
	
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
	    
	    getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tabHost.setCurrentTab(1);
				refresh();
			}
		});
	  
	  
	 }
	
	 private void refresh(){
		 String tabTag = getTabHost().getCurrentTabTag(); 
	     Activity activity = getLocalActivityManager().getActivity(tabTag); 
	     if (activity instanceof GroupThreadScreen){
	    		 GroupThreadScreen gts = (GroupThreadScreen)activity;
	    		 gts.resetAdapter();
	    } 
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
			
			CharSequence colors[] = new CharSequence[] {"From Gallery", "From Camera"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick Source");
			builder.setItems(colors, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
                    if (which == 1){
                    	Intent intent = new Intent(activity, AndroidPhotoCapture.class);
            			intent.putExtra("from", "group");
            			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
            		}else{
            			Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        				startActivityForResult(pickPhoto , REQUEST_IMAGE_PICK);
                    }
			    }
			});
			builder.show();
		}
		
		if (title.equals("GroupText")){
			Intent intent = new Intent(activity, FullScreenTextDrawing.class);
			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		}
		
		if (title.equals("RefreshGroup")){
			  long diff = (System.currentTimeMillis() - lastRunTime) / (1000 * 60);
			  if (lastRunTime !=0 && diff > 3){ 
			      final StreamCategoryObject groupPosts = new StreamCategoryObject("groupphotos");
			      groupPosts.loadStreamObjects(new StreamCallback() {
				  public void result(boolean succeed, String errorMessage) {
				       if (succeed){
				    	   ApplicationInstance.getInstance().setGroupPosts(groupPosts);
				    	   updateUI();
				       }
				   }
			    });
			}
			lastRunTime = System.currentTimeMillis();  
		}

		return super.onOptionsItemSelected(item);

	}
	
	private void updateUI(){
			runOnUiThread(new Runnable(){
				public void run() {
				   refresh();
				}
			});
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
		       
		       
		 case REQUEST_IMAGE_PICK:
		      if(resultCode == RESULT_OK){  
		    	  String path = ImageHandler.getImgPath(imageReturnedIntent.getData(), this);
		    	  Intent intent = new Intent (this, FullScreenImageDrawing.class);
		    	  intent.putExtra("path", path);
		    	  startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		      }     
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
