package com.streamsdk.chat;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.stream.api.StreamCallback;
import com.stream.api.StreamFile;
import com.stream.api.StreamUser;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.addfriend.AddFriendMainActivity;
import com.streamsdk.chat.handler.MessageHistoryHandler;
import com.streamsdk.header.NamesBaseAdaper;
import com.streamsdk.header.PinnedHeaderListView;
import com.streamsdk.xmpp.XMPPConnectionService;

public class MyFriendsActivity extends ListActivity implements RefreshUI{

	private RefreshUI ru;
	private static String [] names = {};
	
	static {
		//Arrays.sort(names);
	}

	//private NamesAdapter mAdapter;
	private NamesBaseAdaper mAdapter;
	private Activity activity;
	
	@Override
    public void onPause()
    {
        super.onPause();
        ApplicationInstance.getInstance().setVisiable(false);
    }
	
	private void reiniDB(){
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	}
	
	protected void onResume(){
	   super.onResume();
	   ApplicationInstance.getInstance().setVisiable(true);
	   FriendDB db = ApplicationInstance.getInstance().getFriendDB();
	   if (db == null){
		   reiniDB();
	   }
	   
	   
	   List<String> tempNames = ApplicationInstance.getInstance().getFriendDB().getFriends();
	   if (tempNames.size() != names.length){
		   names = ApplicationInstance.getInstance().getFriendDB().getFriendsArray();
		 //  mAdapter = new NamesAdapter(this, R.layout.list_item, android.R.id.text1, names);
		   mAdapter = new NamesBaseAdaper(activity, convertToLowerCase(names));
		   setListAdapter(mAdapter);
		   updateData();
	   }
	   
	  /* if ((!StreamXMPP.getInstance().isConnected())){
		   new Thread(new ReconnectThread(this)).start();
	   }*/
		   
	   Log.i("", "");	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		activity =  this;
		ru = this;
		
		 FriendDB db = ApplicationInstance.getInstance().getFriendDB();
		 if (db == null){
			   reiniDB();
			   db =  ApplicationInstance.getInstance().getFriendDB();
		  }
	
		names = db.getFriendsArray();
		
		for (String friendName : names)
			loadMetadataAndProfileImage(friendName);
		
		ApplicationInstance.getInstance().setContext(getApplicationContext());
		
		ApplicationInstance.getInstance().setRefreshUI(this);
		
		new Thread(new MessageHistoryHandler(ApplicationInstance.getInstance().getLoginName(), getApplicationContext(), ru)).start();
		//new Thread(new ConnectionCheck(this)).start();
		
	    setContentView(R.layout.mainpage_layout);
	   // mAdapter = new NamesAdapter(this, R.layout.list_item, android.R.id.text1, convertToLowerCase(names)); 
	    mAdapter = new NamesBaseAdaper(activity, convertToLowerCase(names));
	    
	    setListAdapter(mAdapter);
	    setupListView();
	    
	    Intent intent = new Intent(this, XMPPConnectionService.class);
	    startService(intent);
	}
	
	private void loadMetadataAndProfileImage(String friendName){
		final StreamUser sUser = new StreamUser();
		sUser.loadUserMetadataInbackground(friendName, new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
				Map<String, String> userMetadata = sUser.getUserMetadata();
				ApplicationInstance.getInstance().updateFriendMetadata(sUser.getUserName(), userMetadata);
				String fileId = userMetadata.get(ApplicationInstance.PROFILE_IMAGE);
				if (fileId != null && !fileId.equals("")){
					boolean exists = FileCache.getInstance().generateProfileImagePathIfDoesNotExists(fileId);
					if (!exists){
					    File profileImageFile = FileCache.getInstance().loadFile(fileId);
						StreamFile sf = new StreamFile();
						InputStream in = sf.getFileObject(fileId);
						FileCache.getInstance().writeFileToDisk(profileImageFile, in);
					}
					refresh();	
				}
			}
		});
	}
	
	private String[] convertToLowerCase(String strings[]){
		String newString[] = new String[strings.length];
		int index = 0;
		for (String str : strings){
			newString[index] = str.toLowerCase();
		    index++;
		}
		return newString;
	}
	
	private void setupListView() {
        PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(android.R.id.list);
	    listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_item_header, listView, false));
	    listView.setOnScrollListener(mAdapter);
	    listView.setDividerHeight(0);
	    listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				String name = (String)mAdapter.getItem(position);
				//TODO: may need convert the name to lower case?
				Intent intent = new Intent(activity, MainActivity.class);
	            intent.putExtra("receiver", name);    
	            startActivity(intent);
			}
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	         ApplicationInstance.getInstance().setCurrentChatListener(null);  
	     }
	    return super.onKeyDown(keyCode, event);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		String title = (String) item.getTitle();
		if (title.equals("Refresh")) {
			Intent intent = new Intent(activity, AddFriendMainActivity.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);

	}
	
   public boolean onCreateOptionsMenu(Menu menu){
		 menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 return true;
   }
		
   
   private void updateData(){
		
		runOnUiThread(new Runnable(){
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		});
	 }
	
	 public void refresh() {
		 updateData();	     	 
	 }

}

